/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.module.ld.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.TransientBalanceInquiryAttributes;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.math.BigDecimal;

/**
 * Labor business object for July 1 Position Funding
 */
public class July1PositionFunding extends LedgerBalance {

    private KualiDecimal july1BudgetAmount;
    private BigDecimal july1BudgetFteQuantity;
    private BigDecimal july1BudgetTimePercent;
    private String personName;
    private TransientBalanceInquiryAttributes dummyBusinessObject;

    /**
     * Default constructor.
     */
    public July1PositionFunding() {

    }

    /**
     * Gets the july1BudgetAmount.
     *
     * @return Returns the july1BudgetAmount
     */
    public KualiDecimal getJuly1BudgetAmount() {
        return july1BudgetAmount;
    }

    /**
     * Sets the july1BudgetAmount.
     *
     * @param july1BudgetAmount The july1BudgetAmount to set.
     */
    public void setJuly1BudgetAmount(KualiDecimal july1BudgetAmount) {
        this.july1BudgetAmount = july1BudgetAmount;
    }

    /**
     * Gets the july1BudgetFteQuantity.
     *
     * @return Returns the july1BudgetFteQuantity
     */
    public BigDecimal getJuly1BudgetFteQuantity() {
        return july1BudgetFteQuantity;
    }

    /**
     * Sets the july1BudgetFteQuantity.
     *
     * @param july1BudgetFteQuantity The july1BudgetFteQuantity to set.
     */
    public void setJuly1BudgetFteQuantity(BigDecimal july1BudgetFteQuantity) {
        this.july1BudgetFteQuantity = july1BudgetFteQuantity;
    }

    /**
     * Gets the july1BudgetTimePercent.
     *
     * @return Returns the july1BudgetTimePercent
     */
    public BigDecimal getJuly1BudgetTimePercent() {
        return july1BudgetTimePercent;
    }

    /**
     * Sets the july1BudgetTimePercent.
     *
     * @param july1BudgetTimePercent The july1BudgetTimePercent to set.
     */
    public void setJuly1BudgetTimePercent(BigDecimal july1BudgetTimePercent) {
        this.july1BudgetTimePercent = july1BudgetTimePercent;
    }

    /**
     * Gets the dummyBusinessObject.
     *
     * @return Returns the dummyBusinessObject.
     */
    @Override
    public TransientBalanceInquiryAttributes getDummyBusinessObject() {
        return dummyBusinessObject;
    }

    /**
     * Sets the dummyBusinessObject.
     *
     * @param dummyBusinessObject The dummyBusinessObject to set.
     */
    @Override
    public void setDummyBusinessObject(TransientBalanceInquiryAttributes dummyBusinessObject) {
        this.dummyBusinessObject = dummyBusinessObject;
    }

    /**
     * Gets the person name.
     * @return the person name
     */
    public String getName() {
        /*
        Person person = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(getEmplid());
        if (person == null) {
            return LaborConstants.BalanceInquiries.UnknownPersonName;
        }

        return person.getName();
        */

        /*
         * KFSCNTRB-1344
         * Replace the above logic that uses PersonService with the following one that uses IdentityService, since the former is a lot slower.
         */
        String name = SpringContext.getBean(FinancialSystemUserService.class).getPersonNameByEmployeeId(getEmplid());
        if (!StringUtils.isEmpty(name)) {
            return name;
        }
        return LaborConstants.BalanceInquiries.UnknownPersonName;
    }

    /**
     * Sets the personName
     *
     * @param personName
     */
    public void setName(String personName) {
        this.personName = personName;
    }
}

