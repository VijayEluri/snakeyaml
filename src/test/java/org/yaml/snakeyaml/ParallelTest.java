/**
 * Copyright (c) 2008-2010 Andrey Somov
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
package org.yaml.snakeyaml;

import java.io.IOException;

import junit.framework.TestCase;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * Test that Yaml instances are independent and can be used in multiple threads.
 */
public class ParallelTest extends TestCase {
    private int progress = 0;
    private int MAX = 5;

    public void testPerfomance() throws IOException {
        String doc = Util.getLocalResource("specification/example2_27.yaml");
        for (int i = 0; i < MAX; i++) {
            Worker worker = new Worker(i, doc);
            Thread thread = new Thread(worker);
            thread.start();
        }
        while (progress < MAX - 1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        }
    }

    private class Worker implements Runnable {
        private int id;
        private String doc;

        public Worker(int id, String doc) {
            this.id = id;
            this.doc = doc;
        }

        public void run() {
            System.out.println("Started: " + id);
            Loader loader = new Loader(new Constructor(Invoice.class));
            Yaml yaml = new Yaml(loader);
            long time1 = System.nanoTime();
            int cycles = 200;
            for (int i = 0; i < cycles; i++) {
                Invoice invoice = (Invoice) yaml.load(doc);
                assertNotNull(invoice);
            }
            long time2 = System.nanoTime();
            float duration = ((time2 - time1) / 1000000) / (float) cycles;
            System.out.println("Duration of " + id + " was " + duration + " ms/load.");
            progress++;
        }
    }

    public void testSharedLoader() throws IOException {
        Loader loader = new Loader(new Constructor(Invoice.class));
        new Yaml(loader);
        try {
            new Yaml(loader);
            fail("Loader cannot be shared.");
        } catch (YAMLException e) {
            assertEquals("Loader cannot be shared.", e.getMessage());
        }
    }

    public void testSharedDumper() throws IOException {
        Dumper dumper = new Dumper(new DumperOptions());
        new Yaml(dumper);
        try {
            new Yaml(dumper);
            fail("Dumper cannot be shared.");
        } catch (YAMLException e) {
            assertEquals("Dumper cannot be shared.", e.getMessage());
        }
    }
}
