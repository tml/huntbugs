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
package one.util.huntbugs.testdata;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;

import one.util.huntbugs.registry.anno.AssertNoWarning;
import one.util.huntbugs.registry.anno.AssertWarning;

/**
 * @author Tagir Valeev
 *
 */
public class TestLockProblems {
    @AssertWarning("IncorrectConcurrentMethod")
    public void waitForCondition(Condition cond) throws InterruptedException {
        cond.wait(1);
    }

    @AssertWarning("IncorrectConcurrentMethod")
    public void notifyCondition(Condition cond) {
        cond.notify();
    }

    @AssertNoWarning("IncorrectConcurrentMethod")
    public void waitForObject(Object cond) throws InterruptedException {
        cond.wait();
    }

    @AssertNoWarning("*")
    public void waitForConditionOk(Condition cond) throws InterruptedException {
        cond.await();
    }

    @AssertWarning("IncorrectConcurrentMethod")
    public void notifyCountDown(CountDownLatch latch) {
        latch.notifyAll();
    }
}
