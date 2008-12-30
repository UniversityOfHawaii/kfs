/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.authorization;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.identity.KimAttributes;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.ParameterEvaluator;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * This class...
 */
public class FinancialSystemTransactionalDocumentAuthorizerBase extends TransactionalDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(FinancialSystemTransactionalDocumentAuthorizerBase.class);

    private static RoleService roleService;
    
    private final static String ERROR_CORRECT_DOCUMENT_PERMISSION_NAMESPACE = "KFS-SYS";
    private final static String ERROR_CORRECT_DOCUMENT_PERMISSION_TEMPLATE = "Error Correct Document";

    // TODO fix for kim
//    /**
//     * Adds settings for KFS transactional-document-specific flags.
//     * 
//     * @see org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizerBase#getDocumentActionFlags(Document,
//     *      Person)
//     */
//    @Override
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        if ( LOG.isDebugEnabled() ) {
//            LOG.debug("calling FinancialSystemTransactionalDocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPrincipalName() + "'");
//        }
//        FinancialSystemTransactionalDocumentActionFlags flags = new FinancialSystemTransactionalDocumentActionFlags(super.getDocumentActionFlags(document, user));
//
//        FinancialSystemTransactionalDocument transactionalDocument = (FinancialSystemTransactionalDocument) document;
//        KualiWorkflowDocument workflowDocument = transactionalDocument.getDocumentHeader().getWorkflowDocument();
//
//        flags.setCanErrorCorrect(canErrorCorrect(document, user));
//
//        // if document implements AmountTotaling interface, then we should display the total
//        flags.setHasAmountTotal(document instanceof AmountTotaling);
//
//        // check bank specification is enabled and the code should be viewable for this document type. set flag accordingly
//        boolean bankSpecificationEnabled = SpringContext.getBean(BankService.class).isBankSpecificationEnabled();
//        if (bankSpecificationEnabled) {
//            String documentTypeCode = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(document.getClass());
//            ParameterEvaluator evaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(Bank.class, KFSParameterKeyConstants.BANK_CODE_DOCUMENT_TYPES, documentTypeCode);
//
//            flags.setCanViewBank(evaluator.evaluationSucceeds());
//        }
//
//        // check user can edit bank and set flag accordingly
//        if (flags.getCanViewBank()) {
//            String editBankGroupName = SpringContext.getBean(ParameterService.class).getParameterValue(Bank.class, KFSParameterKeyConstants.BANK_EDITABLE_GROUP);
//            KimGroup editBankGroup = getIdentityManagementService().getGroupByName(org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, editBankGroupName);
//            if (editBankGroup != null) {
//                flags.setCanEditBank(getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), editBankGroup.getGroupId()));
//            } else {
//                LOG.error("Group given for parameter" + KFSParameterKeyConstants.BANK_EDITABLE_GROUP + " is not valid: " + editBankGroupName);
//                throw new RuntimeException("Group given for parameter" + KFSParameterKeyConstants.BANK_EDITABLE_GROUP + " is not valid: " + editBankGroupName);
//            }
//        }
//
//        return flags;
//    }
    
    /**
     * Overridden to check if document error correction can be allowed here.
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getDocumentActions(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person, java.util.Set)
     */
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        Set<String> documentActionsToReturn = super.getDocumentActions(document, user, documentActionsFromPresentationController);
        if (documentActionsFromPresentationController.contains(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT) && canErrorCorrect(document, user)) {
            documentActionsToReturn.add(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT);
        }
        return documentActionsToReturn;
    }

    /**
     * Determines if the KIM permission is available to error correct the given document
     * @param document the document to correct
     * @param user the user to check error correction for
     * @return true if the user can error correct, false otherwise
     */
    public boolean canErrorCorrect(Document document, Person user){
         return isAuthorizedByTemplate(document, FinancialSystemTransactionalDocumentAuthorizerBase.ERROR_CORRECT_DOCUMENT_PERMISSION_NAMESPACE, FinancialSystemTransactionalDocumentAuthorizerBase.ERROR_CORRECT_DOCUMENT_PERMISSION_TEMPLATE, user.getPrincipalId());
    }

    @Override
    protected void populateRoleQualification(Document document, Map<String,String> attributes) {
        super.populateRoleQualification(document, attributes);
        
        // get the KFS-SYS User qualifiers
        // get the namespace from the current qualifier set (set by the superclass)
        String namespaceCode = attributes.get(KimAttributes.NAMESPACE_CODE);
        Person initiator = getPersonService().getPersonByPrincipalName( document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId() );

        // build a new attribute set for checking the role service
        AttributeSet userRoleQualifiers = new AttributeSet();
        userRoleQualifiers.put(KimAttributes.NAMESPACE_CODE, namespaceCode);        
        List<AttributeSet> userRoleInfo = getRoleService().getRoleQualifiersForPrincipal(initiator.getPrincipalId(), 
                KFSConstants.ParameterNamespaces.KFS, KimConstants.KIM_ROLE_NAME_USER, userRoleQualifiers);

        if ( userRoleInfo != null && !userRoleInfo.isEmpty() ) {
            attributes.put(KimAttributes.CHART_OF_ACCOUNTS_CODE, userRoleInfo.get(0).get(KimAttributes.CHART_OF_ACCOUNTS_CODE));
            attributes.put(KimAttributes.ORGANIZATION_CODE, userRoleInfo.get(0).get(KimAttributes.ORGANIZATION_CODE));
        }
        attributes.put(KimAttributes.CAMPUS_CODE, initiator.getCampusCode());
        
    }

    public static RoleService getRoleService() {
        if ( roleService == null ) {
            roleService = KIMServiceLocator.getRoleManagementService();
        }
        return roleService;
    }


}

