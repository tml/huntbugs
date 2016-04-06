/*
 * Copyright 2015, 2016 Tagir Valeev
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

import com.strobel.decompiler.ast.AstCode;
import com.strobel.decompiler.ast.Expression;

import one.util.huntbugs.registry.MethodContext;
import one.util.huntbugs.registry.anno.AstExpressionVisitor;
import one.util.huntbugs.registry.anno.WarningDefinition;
import one.util.huntbugs.util.Nodes;

/**
 * @author lan
 *
 */
@WarningDefinition(category = "Correctness", name = "SelfComputation", baseScore = 70)
public class SelfComputation {
	@AstExpressionVisitor
	public void visit(Expression expr, MethodContext mc) {
		if (expr.getCode() == AstCode.And || expr.getCode() == AstCode.Or
				|| expr.getCode() == AstCode.Sub
				|| expr.getCode() == AstCode.Div
				|| expr.getCode() == AstCode.Rem) {
		    if(expr.getArguments().size() == 2 && Nodes.isEquivalent(expr.getArguments().get(0), expr.getArguments().get(1))) {
		        mc.report("SelfComputation", 0, expr);
		    }
		}
	}
}