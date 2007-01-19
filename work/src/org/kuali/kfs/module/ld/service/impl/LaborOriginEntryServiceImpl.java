/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.dao.LaborOriginEntryDao;
import org.kuali.module.labor.service.LaborOriginEntryService;
import org.kuali.module.labor.util.LaborLedgerUnitOfWork;
import org.kuali.module.labor.util.ObjectUtil;

/**
 * This class...
 */
public class LaborOriginEntryServiceImpl implements LaborOriginEntryService {
    
    private LaborOriginEntryDao originEntryDao;

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getEntriesByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group) {
        return originEntryDao.getEntriesByGroup(group);
    }

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getEntriesByGroups(java.util.Collection)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroups(Collection<OriginEntryGroup> groups) {
        return originEntryDao.getEntriesByGroups(groups);
    }

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getEntriesByGroup(org.kuali.module.gl.bo.OriginEntryGroup,
     *      boolean)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group, boolean isConsolidated) {
        if(!isConsolidated){
            return this.getEntriesByGroup(group);
        }
        
        Collection<LaborOriginEntry> entryCollection = new ArrayList<LaborOriginEntry>(); 
        LaborLedgerUnitOfWork laborLedgerUnitOfWork = new LaborLedgerUnitOfWork();
        
        Iterator<Object[]> consolidatedEntries = originEntryDao.getConsolidatedEntriesByGroup(group);
        while(consolidatedEntries.hasNext()){
            LaborOriginEntry laborOriginEntry = new LaborOriginEntry();
            Object[] oneEntry = consolidatedEntries.next();
            ObjectUtil.buildObject(laborOriginEntry, oneEntry, LaborConstants.consolidationAttributesOfOriginEntry());
            
            if(laborLedgerUnitOfWork.canContain(laborOriginEntry)){
                laborLedgerUnitOfWork.addEntryIntoUnit(laborOriginEntry);
            }
            else{
                entryCollection.add(laborLedgerUnitOfWork.getWorkingEntry());
                laborLedgerUnitOfWork.resetLaborLedgerUnitOfWork(laborOriginEntry);
            }           
        }
        return entryCollection.iterator();
    }

    /**
     * Sets the originEntryDao attribute value.
     * 
     * @param originEntryDao The originEntryDao to set.
     */
    public void setOriginEntryDao(LaborOriginEntryDao originEntryDao) {
        this.originEntryDao = originEntryDao;
    }
}