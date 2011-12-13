/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.businessobject.options;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;

/**
 * returns list of accounting period value pairs valid for year end posting
 */
public class YearEndAccountingPeriodValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        List<KeyLabelPair> accountingPeriodCodes = new ArrayList<KeyLabelPair>();

        Date date = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        AccountingPeriod currentAccountingPeriod = SpringContext.getBean(AccountingPeriodService.class).getByDate(date);

        if (currentAccountingPeriod.isOpen()) {
            // add the current period with blank value, so scrubber will set entries when posting
            accountingPeriodCodes.add(new KeyLabelPair("", currentAccountingPeriod.getUniversityFiscalPeriodName()));
        }

        String numberOfPostbackPeriodsParmVal = SpringContext.getBean(ParameterService.class).getParameterValue(KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KFSConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.NUMBER_OF_POSTBACK_PERIODS);
        if (StringUtils.isNotBlank(numberOfPostbackPeriodsParmVal) && Integer.valueOf(numberOfPostbackPeriodsParmVal) > 0) {
            for (int i = 1; i <= Integer.valueOf(numberOfPostbackPeriodsParmVal); i++) {
                int currentFiscalYear = currentAccountingPeriod.getUniversityFiscalYear();
                int currentFiscalPeriod = Integer.valueOf(currentAccountingPeriod.getUniversityFiscalPeriodCode());

                if (currentFiscalPeriod == 1) {
                    currentFiscalYear = currentFiscalYear - 1;
                    currentFiscalPeriod = 13;
                } else {
                    currentFiscalPeriod = currentFiscalPeriod - 1;
                }

                currentAccountingPeriod = SpringContext.getBean(AccountingPeriodService.class).getByPeriod(StringUtils.leftPad(Integer.toString(currentFiscalPeriod), 2, "0"), currentFiscalYear);
                if (currentAccountingPeriod.isOpen()) {
                    accountingPeriodCodes.add(new KeyLabelPair(currentAccountingPeriod.getUniversityFiscalPeriodCode() + currentAccountingPeriod.getUniversityFiscalYear(), currentAccountingPeriod.getUniversityFiscalPeriodName()));
                }
            }
        }

        return accountingPeriodCodes;
    }
}