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
package org.kuali.kfs.module.endow.fixture;

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiInteger;

public enum HoldingTaxLotRebalanceFixture {
    // Holding Tax Lot Rebalance Fixture
    HOLDING_TAX_LOT_REBALANCE_RECORD("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "0NI", //registrationCode
            "I", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(20.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_2("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "0AI", //registrationCode
            "I", //incomePrincipalIndicator
            new KualiInteger(2), //totalLotNumber
            BigDecimal.valueOf(282586.00), // totalUnits
            BigDecimal.valueOf(282586.00) // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_3("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "0AI", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(3), //totalLotNumber
            BigDecimal.valueOf(23123.00), // totalUnits
            BigDecimal.valueOf(23123.00) // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_4("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "REI", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(4), //totalLotNumber
            BigDecimal.valueOf(10000.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_LIABILITY("TESTKEMID", //kemid
            "TESTSECID", //securityId
            "TEST", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(20.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost    
    ),
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_EAD("TESTKEMID", //kemid
            "TESTSECID", //securityId
            "TEST", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(20.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost    
    ),
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_ACCRUAL_PROC("TESTKEMID", //kemid
            "TESTSECID", //securityId
            "TEST", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(20.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost    
    );
    
    public final String kemid;
    public final String securityId;
    public final String registrationCode;
    public final String incomePrincipalIndicator; 
    public final KualiInteger totalLotNumber;
    public final BigDecimal totalUnits;
    public final BigDecimal totalCost;

    //default record...
    private HoldingTaxLotRebalanceFixture(String kemid, String securityId,
                                          String registrationCode, String incomePrincipalIndicator, 
                                          KualiInteger totalLotNumber, BigDecimal totalUnits,
                                          BigDecimal totalCost) {
        this.kemid = kemid;
        this.securityId = securityId;
        this.registrationCode = registrationCode;
        this.incomePrincipalIndicator = incomePrincipalIndicator; 
        this.totalLotNumber = totalLotNumber;
        this.totalUnits = totalUnits;
        this.totalCost = totalCost;

    }

    /**
     * Default record...
     * This method creates a HoldingHistoryRebalance record and saves it to table
     * @return holdingTaxLotRebalance record
     */
    public HoldingTaxLotRebalance createHoldingTaxLotRebalanceRecord() {
        HoldingTaxLotRebalance holdingTaxLotRebalance = new HoldingTaxLotRebalance();

        holdingTaxLotRebalance.setKemid(kemid);
        holdingTaxLotRebalance.setSecurityId(securityId);
        holdingTaxLotRebalance.setRegistrationCode(registrationCode);
        holdingTaxLotRebalance.setIncomePrincipalIndicator(incomePrincipalIndicator);
        holdingTaxLotRebalance.setTotalLotNumber(totalLotNumber);
        holdingTaxLotRebalance.setTotalUnits(totalUnits);
        holdingTaxLotRebalance.setTotalCost(totalCost);

        saveHoldingTaxLotRebalanceRecord(holdingTaxLotRebalance);
        
        return holdingTaxLotRebalance;
    }
    
    /**
     * Method to save the business object....
     */
    private void saveHoldingTaxLotRebalanceRecord(HoldingTaxLotRebalance holdingTaxLotRebalance) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(holdingTaxLotRebalance);
    }
}

