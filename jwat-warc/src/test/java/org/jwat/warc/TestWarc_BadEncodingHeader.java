/**
 * Java Web Archive Toolkit - Software to read and validate ARC, WARC
 * and GZip files. (http://jwat.org/)
 * Copyright 2011-2012 Netarkivet.dk (http://netarkivet.dk/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jwat.warc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestWarc_BadEncodingHeader {

    private int expected_records;
    private int expected_errors;
    private int expected_warnings;
    private String warcFile;

    @Parameters
    public static Collection<Object[]> configs() {
        return Arrays.asList(new Object[][] {
                {4, 2, 0, "invalid-warcfile-encoding-headers.warc.gz"}
        });
    }

    public TestWarc_BadEncodingHeader(int records, int errors, int warnings, String warcFile) {
        this.expected_records = records;
        this.expected_errors = errors;
        this.expected_warnings = warnings;
        this.warcFile = warcFile;
    }

    @Ignore
    @Test
    public void test_bad_encoding_warc_header() {
        boolean bDebugOutput = System.getProperty("jwat.debug.output") != null;

        InputStream in;

        int records = 0;
        int errors = 0;
        int warnings = 0;

        try {
            in = TestHelpers.getTestResourceAsStream(warcFile);

            WarcReader reader = WarcReaderFactory.getReader(in);
            WarcRecord record;

            while ((record = reader.getNextRecord()) != null) {
                if (bDebugOutput) {
                    TestBaseUtils.printRecord(record);
                    TestBaseUtils.printRecordErrors(record);
                }

                record.close();

                Assert.assertNull(record.getHeader(null));
                Assert.assertNull(record.getHeader(""));

                if (record.diagnostics.hasErrors()) {
                    errors += record.diagnostics.getErrors().size();
                }

                if (record.diagnostics.hasWarnings()) {
                    warnings += record.diagnostics.getWarnings().size();
                }

                ++records;
            }

            reader.close();
            in.close();

            if (bDebugOutput) {
                TestBaseUtils.printStatus(records, errors, warnings);
            }
        } catch (FileNotFoundException e) {
            Assert.fail("Input file missing");
        } catch (IOException e) {
            Assert.fail("Unexpected I/O exception");
        }

        Assert.assertEquals(expected_records, records);
        Assert.assertEquals(expected_errors, errors);
        Assert.assertEquals(expected_warnings, warnings);
    }

}
