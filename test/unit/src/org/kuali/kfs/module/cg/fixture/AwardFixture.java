/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.fixture;

import java.sql.Date;

import org.kuali.kfs.integration.cg.CGIntegrationConstants;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.rice.core.api.util.type.KualiDecimal;
/**
 * Fixture class for Award
 */
public enum AwardFixture {

    CG_AWARD1(new Long(111), "2011-10-01", "2011-09-22", null, null, null, false, null, true, null),
    CG_AWARD2(new Long(11), "1968-07-01", "1969-06-30", new KualiDecimal(7708.00), new KualiDecimal(2016.00), KualiDecimal.ZERO, false, null, true, null),
    CG_AWARD3(new Long(1234), "2011-01-01", "2011-09-22", new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, null, true, null),

    CG_AWARD_INV_AWARD(new Long(111), "2011-10-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, null),
    CG_AWARD_INV_ACCOUNT(new Long(111), "2011-10-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.ACCOUNT.getCode(), true, CGConstants.MONTHLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_INV_CCA(new Long(111), "2011-10-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.CONTRACT_CONTROL.getCode(), true, null),

    CG_AWARD_MONTHLY_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.MONTHLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_MILESTONE_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.MILESTONE_BILLING_SCHEDULE_CODE),
    CG_AWARD_PREDETERMINED_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.PREDETERMINED_BILLING_SCHEDULE_CODE),
    CG_AWARD_QUAR_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.QUATERLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_SEMI_ANN_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_ANNUAL_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.ANNUALLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_LOCB_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.LOC_BILLING_SCHEDULE_CODE),
    CG_AWARD_MONTHLY_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.MONTHLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_MILESTONE_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.MILESTONE_BILLING_SCHEDULE_CODE),
    CG_AWARD_PREDETERMINED_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.PREDETERMINED_BILLING_SCHEDULE_CODE),
    CG_AWARD_QUAR_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.QUATERLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_SEMI_ANN_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_ANNUAL_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.ANNUALLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_LOCB_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.LOC_BILLING_SCHEDULE_CODE);

    private Long proposalNumber;
    private String awardBeginningDate;
    private String awardEndingDate;

    private KualiDecimal awardDirectCostAmount;
    private KualiDecimal awardIndirectCostAmount;
    private KualiDecimal minInvoiceAmount;
    private boolean excludedFromInvoicing;
    private String invoicingOptionCode;
    private boolean active;
    private String billingFrequencyCode;

    private AwardFixture(Long proposalNumber, String awardBeginningDate, String awardEndingDate, KualiDecimal awardDirectCostAmount, KualiDecimal awardIndirectCostAmount, KualiDecimal minInvoiceAmount, boolean excludedFromInvoicing, String invoicingOptionCode, boolean active, String billingFrequencyCode) {

        this.proposalNumber = proposalNumber;
        this.awardBeginningDate = awardBeginningDate;
        this.awardEndingDate = awardEndingDate;
        this.awardDirectCostAmount = awardDirectCostAmount;
        this.awardIndirectCostAmount = awardIndirectCostAmount;
        this.minInvoiceAmount = minInvoiceAmount;
        this.excludedFromInvoicing = excludedFromInvoicing;
        this.invoicingOptionCode = invoicingOptionCode;
        this.active = active;
        this.billingFrequencyCode = billingFrequencyCode;
    }

    public Award createAward() {
        Award award = new Award();
        award.setProposalNumber(this.proposalNumber);
        award.setAwardBeginningDate(Date.valueOf(this.awardBeginningDate));
        award.setAwardEndingDate(Date.valueOf(this.awardEndingDate));
        award.setAwardDirectCostAmount(this.awardDirectCostAmount);
        award.setAwardIndirectCostAmount(this.awardIndirectCostAmount);
        award.setMinInvoiceAmount(this.minInvoiceAmount);
        award.setExcludedFromInvoicing(this.excludedFromInvoicing);
        award.setInvoicingOptionCode(this.invoicingOptionCode);
        award.setActive(this.active);
        award.setBillingFrequencyCode(this.billingFrequencyCode);
        return award;
    }


}
