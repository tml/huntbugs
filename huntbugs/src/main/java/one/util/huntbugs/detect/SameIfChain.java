/*
 * Copyright 2016 HuntBugs contributors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package one.util.huntbugs.detect;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.strobel.decompiler.ast.AstCode;
import com.strobel.decompiler.ast.Block;
import com.strobel.decompiler.ast.Condition;
import com.strobel.decompiler.ast.Expression;
import com.strobel.decompiler.ast.Node;
import com.strobel.decompiler.ast.Variable;

import one.util.huntbugs.registry.MethodContext;
import one.util.huntbugs.registry.anno.AstVisitor;
import one.util.huntbugs.registry.anno.WarningDefinition;
import one.util.huntbugs.util.Equi;
import one.util.huntbugs.util.Nodes;
import one.util.huntbugs.warning.Role.LocationRole;

/**
 * @author lan
 *
 */
@WarningDefinition(category = "RedundantCode", name = "SameConditionChain", maxScore = 50)
public class SameIfChain {
    private static final LocationRole SAME_CONDITION_AT = LocationRole.forName("SAME_CONDITION_AT");
    
    @AstVisitor
    public boolean visit(Node node, MethodContext mc) {
        if (!mc.isAnnotated())
            return false;
        if (node instanceof Block) {
            List<Node> body = ((Block) node).getBody();
            if(!body.isEmpty()) {
                Node second = body.get(0);
                for (int i = 1; i < body.size(); i++) {
                    Node first = second;
                    second = body.get(i);
                    if (first instanceof Condition && second instanceof Condition) {
                        Condition cond1 = (Condition) first;
                        Condition cond2 = (Condition) second;
                        if (!cond1.getFalseBlock().getBody().isEmpty())
                            continue;
                        Expression c1 = cond1.getCondition();
                        Expression c2 = cond2.getCondition();
                        if (Nodes.isPure(c1) && Equi.equiExpressions(c1, c2)) {
                            Set<Variable> vars = Nodes.stream(c1).filter(e -> e.getCode() == AstCode.Load).map(
                                e -> (Variable) e.getOperand()).collect(Collectors.toSet());
                            if (Nodes.find(cond1.getTrueBlock(), n -> Nodes.isWriteTo(n, vars)) != null)
                                continue;
                            int priority = 0;
                            if(!cond1.getTrueBlock().getBody().isEmpty() &&
                                    cond1.getTrueBlock().getBody().get(0) instanceof Condition) {
                                priority += 15;
                            }
                            mc.report("SameConditionChain", priority, c1, SAME_CONDITION_AT.create(mc, c2));
                        }
                    }
                }
            }
        }
        return true;
    }
}
