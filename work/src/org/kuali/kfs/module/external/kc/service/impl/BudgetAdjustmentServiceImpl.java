/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentSourceAccountingLine;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentTargetAccountingLine;
import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.dto.BudgetAdjustmentCreationStatusDTO;
import org.kuali.kfs.module.external.kc.dto.BudgetAdjustmentParametersDTO;
import org.kuali.kfs.module.external.kc.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.dto.KcObjectCode;
import org.kuali.kfs.module.external.kc.service.BudgetAdjustmentService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BudgetAdjustmentServiceImpl implements BudgetAdjustmentService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentServiceImpl.class);
    
    private DocumentService documentService;
    private ParameterService parameterService;
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;

    /**
     * This is the web service method that facilitates budget adjustment  
     * 1. Creates a Budget Adjustment Doc using the parameters from KC 
     * 2. Returns the status object
     * 
     * @param BudgetAdjustmentParametersDTO
     * @return BudgetAdjustmentStatusDTO
     */
    public BudgetAdjustmentCreationStatusDTO createBudgetAdjustment(BudgetAdjustmentParametersDTO budgetAdjustmentParameters) {
        
        BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatus = new BudgetAdjustmentCreationStatusDTO();
        budgetAdjustmentCreationStatus.setErrorMessages(new ArrayList<String>());
        budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_SUCCESS);
                
        // check to see if the user has the permission to create account
        String principalId = budgetAdjustmentParameters.getPrincipalId();
        if (!isValidUser(principalId)) {
            budgetAdjustmentCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_NOT_ALLOWED_TO_CREATE_CG_MAINTENANCE_DOCUMENT);
            budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
            return budgetAdjustmentCreationStatus;
        }
                
        // create an Budget Adjustment object        
        BudgetAdjustmentDocument budgetAdjustmentDoc = createBudgetAdjustmentObject(budgetAdjustmentParameters, budgetAdjustmentCreationStatus);
      
        // set required values to AccountCreationStatus
        if (budgetAdjustmentCreationStatus.getStatus().equals(KcConstants.KcWebService.STATUS_KC_SUCCESS) && getDocumentService().documentExists(budgetAdjustmentDoc.getDocumentHeader().getDocumentNumber())) {
            routeBudgetAdjustmentDocument(budgetAdjustmentDoc, budgetAdjustmentCreationStatus);  
            budgetAdjustmentCreationStatus.setDocumentNumber(budgetAdjustmentDoc.getDocumentNumber());
        }      
        return budgetAdjustmentCreationStatus;
    }
  
    
    /**
     * 
     * This method creates an account to be used for automatic maintenance document
     * @param AccountParametersDTO
     * @return Account
     */
    protected BudgetAdjustmentDocument createBudgetAdjustmentObject(BudgetAdjustmentParametersDTO parameters, BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatus) {
         BudgetAdjustmentDocument budgetAdjustmentDocument = (BudgetAdjustmentDocument) createBADocument(budgetAdjustmentCreationStatus);
        //also populates posting year
        budgetAdjustmentDocument.initiateDocument();
        
        //budgetAdjustmentDocument.getDocumentHeader().setDocumentNumber(documentNumber)
        
        //The Description of the BA document should carry the Award Document Number and Budget Version Number.
        budgetAdjustmentDocument.getDocumentHeader().setDocumentDescription(parameters.getAwardDocumentNumber() + " " + parameters.getBudgetVersionNumber());
        //The Comment section of the KC Award Budget Document will carry a BA document number for a reference purpose.
        budgetAdjustmentDocument.getDocumentHeader().setExplanation(parameters.getComment());
        budgetAdjustmentDocument.setPostingPeriodCode(parameters.getPostingPeriodCode());
     //   budgetAdjustmentDocument.setPostingYear(new Integer(parameters.getPostingYear()));
        budgetAdjustmentDocument.getDocumentHeader().setOrganizationDocumentNumber("");
        
         for (BudgetAdjustmentParametersDTO.Details detail : parameters.getDetails()) {
           switch (detail.getLineType().charAt(0)) {
                 case 'F':   budgetAdjustmentDocument.addSourceAccountingLine( createBudgetAdjustmentSourceAccountingLine(detail));
                        break;
                 case 'T':   budgetAdjustmentDocument.addTargetAccountingLine( createBudgetAdjustmentTargetAccountingLine(detail));
                        break;
             }
     
          }
        // save the document 
        try{
            getDocumentService().saveDocument(budgetAdjustmentDocument);
        }catch(WorkflowException wfe){
            LOG.error(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_NOT_SAVED +  wfe.getMessage()); 
            budgetAdjustmentCreationStatus.getErrorMessages().add(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_NOT_SAVED +  wfe.getMessage());
            budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
        } catch (Exception ex) {
   
            LOG.error(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_NOT_SAVED +  ex.getMessage());           
            budgetAdjustmentCreationStatus.setErrorMessages( GlobalVariablesExtractHelper.extractGlobalVariableErrors() );
            budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
           
        }
         
         return budgetAdjustmentDocument;
    }
    
    protected BudgetAdjustmentSourceAccountingLine createBudgetAdjustmentSourceAccountingLine(BudgetAdjustmentParametersDTO.Details detail) {
        BudgetAdjustmentSourceAccountingLine budgetAdjustmentSourceAccountingLine = new BudgetAdjustmentSourceAccountingLine();
        // from / decrease chart -account
       
        budgetAdjustmentSourceAccountingLine.setFinancialDocumentLineTypeCode(detail.getLineType());
        budgetAdjustmentSourceAccountingLine.setChartOfAccountsCode(detail.getChart());
        budgetAdjustmentSourceAccountingLine.setAccountNumber(detail.getAccount());
        budgetAdjustmentSourceAccountingLine.setProjectCode(detail.getProjectCode());
        budgetAdjustmentSourceAccountingLine.setFinancialObjectCode(detail.getObjectCode());
        budgetAdjustmentSourceAccountingLine.setAmount(new KualiDecimal(detail.getAmount()));
        budgetAdjustmentSourceAccountingLine.setCurrentBudgetAdjustmentAmount(new KualiDecimal(detail.getCurrentBudgetAdjustAmount()));
        budgetAdjustmentSourceAccountingLine.setBaseBudgetAdjustmentAmount(new KualiInteger(detail.getBaseBudgetAdjustAmount()));
     
        return budgetAdjustmentSourceAccountingLine;   
 
    }
    
    protected BudgetAdjustmentTargetAccountingLine createBudgetAdjustmentTargetAccountingLine(BudgetAdjustmentParametersDTO.Details detail) {
        BudgetAdjustmentTargetAccountingLine budgetAdjustmentTargetAccountingLine = new BudgetAdjustmentTargetAccountingLine();
        // from / decrease chart -account
        
        budgetAdjustmentTargetAccountingLine.setFinancialDocumentLineTypeCode(detail.getLineType());
        budgetAdjustmentTargetAccountingLine.setChartOfAccountsCode(detail.getChart());
        budgetAdjustmentTargetAccountingLine.setAccountNumber(detail.getAccount());
        budgetAdjustmentTargetAccountingLine.setProjectCode(detail.getProjectCode());
        budgetAdjustmentTargetAccountingLine.setFinancialObjectCode(detail.getObjectCode());
        budgetAdjustmentTargetAccountingLine.setAmount(new KualiDecimal(detail.getAmount()));
        budgetAdjustmentTargetAccountingLine.setCurrentBudgetAdjustmentAmount(new KualiDecimal(detail.getCurrentBudgetAdjustAmount()));
        budgetAdjustmentTargetAccountingLine.setBaseBudgetAdjustmentAmount(new KualiInteger(detail.getBaseBudgetAdjustAmount()));        
        return budgetAdjustmentTargetAccountingLine;   
               
    }

    /**
     * This method will use the DocumentService to create a new document.  The documentTypeName is gathered by
     * using MaintenanceDocumentDictionaryService which uses Account class to get the document type name.
     * 
     * @param AccountCreationStatusDTO
     * @return document  returns a new document for the account document type or null if there is an exception thrown.
     */
    protected Document createBADocument(BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatusDTO) {
        try {
            Document document = getDocumentService().getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName("BA"));
            return document;
        } catch (Exception e) {
            budgetAdjustmentCreationStatusDTO.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            budgetAdjustmentCreationStatusDTO.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
            return null;

            
        }
    }       
   
    /**
     * This method processes the workflow document actions like save, route and blanket approve depending on the 
     * ACCOUNT_AUTO_CREATE_ROUTE system parameter value.
     * If the system parameter value is not of save or submit or blanketapprove, put an error message and quit.
     * Throws an document WorkflowException if the specific document action fails to perform.
     * 
     * @param maintenanceAccountDocument, errorMessages
     * @return success returns true if the workflow document action is successful else return false.
     */
    protected boolean routeBudgetAdjustmentDocument(BudgetAdjustmentDocument budgetAdjustmentDocument, BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatus) {
      
        try {
            //getParameterService().setParameterForTesting(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE, KFSConstants.WORKFLOW_DOCUMENT_ROUTE);

            String BudgetAdjustAutoRouteValue = getParameterService().getParameterValue(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE);
            //String BudgetAdjustAutoRouteValue = getParameterService().getParameterValue(Account.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_BA_DOCUMENT_ROUTE);
            // if the accountAutoCreateRouteValue is not save or submit or blanketApprove then put an error message and quit.
            if (!BudgetAdjustAutoRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE) &&
                !BudgetAdjustAutoRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_ROUTE) &&
                !BudgetAdjustAutoRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) 
            {                
                budgetAdjustmentCreationStatus.getErrorMessages().add(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_DOCUMENT_ACTION_VALUE);
                budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
                return false;
            }
            
            if (BudgetAdjustAutoRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE)) {
                // document already exists and saved
             }
            else if (BudgetAdjustAutoRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {
                getDocumentService().blanketApproveDocument(budgetAdjustmentDocument, "", null); 
            }
            else if (BudgetAdjustAutoRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_ROUTE)) {
                getDocumentService().routeDocument(budgetAdjustmentDocument, "", null);
            }             
            return true;
            
        }  catch (WorkflowException wfe) { 
            LOG.error(KcConstants.BudgetAdjustmentService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS + " -- User: " + GlobalVariables.getUserSession().getPrincipalName() + " -- " + wfe.getMessage()); 
            budgetAdjustmentCreationStatus.getErrorMessages().add(KcConstants.BudgetAdjustmentService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS + " -- User: " + GlobalVariables.getUserSession().getPrincipalName() + " -- " + wfe.getMessage());
            budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_WARNING);
            return false;

        }  catch (Exception ex) { 
            LOG.error(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  ex.getMessage()); 
            budgetAdjustmentCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_WARNING);
            return false;
        }
    }
    
    
    /**
     * This method check to see if the user can create the account maintenance document and set the user session
     * @param String principalId
     * @return boolean
     */
    /*
     protected boolean isValidUser(String principalId) {
         PersonService<Person> personService = KIMServiceLocator.getPersonService();
         try {
             Person user = personService.getPerson(principalId);
             DocumentAuthorizer documentAuthorizer = new MaintenanceDocumentAuthorizerBase();
             if (documentAuthorizer.canInitiate(DocumentTypeAttributes.ACCOUNTING_DOCUMENT_TYPE_NAME, user)) {
                 // set the user session so that the user name can be displayed in the saved document        
                 GlobalVariables.setUserSession(new UserSession(user.getPrincipalName()));
                 return true;
             } 
         } catch (Exception ex) {
             LOG.error(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_NOT_ALLOWED_TO_CREATE_CG_MAINTENANCE_DOCUMENT + principalId) ;
             return false;    
         }

         return false;                
     }
     */
        protected boolean isValidUser(String principalId) {
            
            PersonService<Person> personService = KIMServiceLocator.getPersonService();
           
            try {
                Person user = personService.getPerson(principalId);
               // Person user = personService.getPersonByPrincipalName(principalId);
                DocumentAuthorizer documentAuthorizer = new TransactionalDocumentAuthorizerBase();                
                if (documentAuthorizer.canInitiate(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Account.class), user)) {
                    // set the user session so that the user name can be displayed in the saved document        
                    GlobalVariables.setUserSession(new UserSession(user.getPrincipalName()));
                    return true;
                } else {
                    return false;
                }
            } catch (Exception ex) {
                LOG.error(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_NOT_ALLOWED_TO_CREATE_CG_MAINTENANCE_DOCUMENT + principalId) ;
                return false;    
            }
        }

    
    /**
     * Gets the documentService attribute.
     * 
     * @return Current value of documentService.
     */
    protected DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */    
    protected ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * @see org.kuali.kfs.module.external.kc.service.BudgetAdjustmentService#lookupObjectCodes(java.util.HashMap)
     */

    public List<KcObjectCode> lookupObjectCodes(java.util.List<HashMapElement> searchCriteria) {
        HashMap <String, String> hashMap = new HashMap();
        List <ObjectCode> objCodeList = new ArrayList<ObjectCode>();       
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        
        if ((searchCriteria == null) || (searchCriteria.size() == 0)) {
            objCodeList = (List<ObjectCode>) boService.findAll(ObjectCode.class);
            
        } else {
            for (HashMapElement hashMapElement: searchCriteria) {
                hashMap.put(hashMapElement.getKey(), hashMapElement.getValue());
            }
            objCodeList = (List<ObjectCode>) (boService.findMatching(ObjectCode.class, hashMap));
        }
        List <KcObjectCode> kcObjectCodeList = new ArrayList();
        for (ObjectCode objectCode : objCodeList) {
            kcObjectCodeList.add( createKcObjectCode(objectCode));
        }
        return kcObjectCodeList;
    }
    
    /**
     * 
        @see org.kuali.kfs.module.external.kc.service.getObjectCode(String, String, String)
     */
    public KcObjectCode getObjectCode(String universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        Integer fiscalYear = new Integer(universityFiscalYear);
        ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        ObjectCode objectCode = (ObjectCode) objectCodeService.getByPrimaryId(fiscalYear, chartOfAccountsCode, financialObjectCode);
        return createKcObjectCode(objectCode);
    }
 
    protected KcObjectCode createKcObjectCode(ObjectCode objectCode) {
        KcObjectCode kcObjectCode = new KcObjectCode();
        kcObjectCode.setObjectCodeName(objectCode.getCode());
        kcObjectCode.setBudgetCategoryCode(objectCode.getRschBudgetCategoryCode());
        kcObjectCode.setDescription(objectCode.getName());
        kcObjectCode.setOnOffCampusFlag(objectCode.isRschOnCampusIndicator());
        return kcObjectCode;
    }
    
    /**
     * Extracts errors for error report writing.
     * 
     * @return a list of error messages
     */
 
}
