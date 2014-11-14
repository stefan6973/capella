/*******************************************************************************
 * Copyright (c) 2006, 2014 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *    Thales - initial API and implementation
 *******************************************************************************/
package org.polarsys.capella.core.data.core.validation.constraint;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.EMFEventType;
import org.eclipse.emf.validation.IValidationContext;
import org.polarsys.capella.common.data.modellingcore.AbstractType;
import org.polarsys.capella.common.data.modellingcore.ValueSpecification;
import org.polarsys.capella.core.data.capellacore.Constraint;
import org.polarsys.capella.core.data.information.datatype.BooleanType;
import org.polarsys.capella.core.validation.rule.AbstractValidationRule;

/**
 */
public class MDCHK_Constraint_Expression extends AbstractValidationRule {
  /**
   * FIXME Validation not updated for Opaque Expression
   * @see org.eclipse.emf.validation.AbstractModelConstraint#validate(org.eclipse.emf.validation.IValidationContext)
   */
  @Override
  public IStatus validate(IValidationContext ctx) {
    EObject eObj = ctx.getTarget();
    EMFEventType eType = ctx.getEventType();
    if (eType == EMFEventType.NULL) {
      if (eObj instanceof Constraint) {
        Constraint cst = (Constraint) eObj;
        ValueSpecification vspec = cst.getOwnedSpecification();
        if (null != vspec) {
          AbstractType type = vspec.getAbstractType();
          if (!(type instanceof BooleanType)) {
            return ctx.createFailureStatus(new Object[] { cst.getName() });
          }
        }
      }
    }
    return ctx.createSuccessStatus();
  }
}
