/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ld.batch;

import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.BatchSortUtil;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntryFieldUtil;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.springframework.util.StopWatch;

/**
 * A step to run the scrubber process.
 */
public class LaborDemergerSortStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborDemergerSortStep.class);
    private String batchFileDirectoryName;

    /**
     * Runs the scrubber process.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);
        String inputFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String outputFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        BatchSortUtil.sortTextFileWithFields(inputFile, outputFile, new LaborDemergerSortComparator());

        // sorting reverse order for TransactionEntrySequenceNumber
        // BatchSortUtil.sortTextFileWithFields(batchFileDirectoryName+"/tempsort", batchFileDirectoryName+"/sorterr1", new
        // LaborDemergerSortComparator());

        stopWatch.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug("scrubber step of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
        }
        return true;
    }


    public static class LaborDemergerSortComparator implements Comparator {

        public int compare(Object object1, Object object2) {
            
            LaborOriginEntryFieldUtil loefu = new LaborOriginEntryFieldUtil();
            Map<String, Integer> pMap = loefu.getFieldBeginningPositionMap();
            
            String string1 = (String) object1;
            String string2 = (String) object2;
            StringBuffer sb1 = new StringBuffer();

            sb1.append(string1.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER)));
            // sb1.append(string1.substring(51, 56)); // reverse???
            StringBuffer sb2 = new StringBuffer();
            sb2.append(string1.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER)));
            // sb2.append(string1.substring(51, 56));

            int returnValue = sb1.toString().compareTo(sb2.toString());
            if (returnValue == 0) {
                sb1.append(string1.substring(pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER), pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC))); // reverse???
                sb2.append(string1.substring(pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER), pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC)));
                returnValue = sb2.toString().compareTo(sb1.toString());
            }
            return returnValue;
        }
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
