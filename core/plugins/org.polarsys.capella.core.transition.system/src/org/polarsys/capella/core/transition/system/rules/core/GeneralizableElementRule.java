/*******************************************************************************
 * Copyright (c) 2006, 2016 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *    Thales - initial API and implementation
 *******************************************************************************/

package org.polarsys.capella.core.transition.system.rules.core;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.polarsys.capella.core.data.capellacore.GeneralizableElement;
import org.polarsys.capella.core.data.capellacore.CapellacorePackage;
import org.polarsys.capella.core.transition.system.rules.AbstractCapellaElementRule;
import org.polarsys.kitalpha.transposer.rules.handler.rules.api.IContext;

public class GeneralizableElementRule extends AbstractCapellaElementRule {

  public GeneralizableElementRule() {
    super();
    registerUpdatedAttribute(CapellacorePackage.Literals.GENERALIZABLE_ELEMENT__ABSTRACT);
  }

  @Override
  protected EClass getSourceType() {
    return CapellacorePackage.Literals.GENERALIZABLE_ELEMENT;
  }

  @Override
  protected void retrieveGoDeep(EObject source, List<EObject> result, IContext context) {
    super.retrieveGoDeep(source, result, context);
    GeneralizableElement element = (GeneralizableElement) source;
    result.addAll(element.getOwnedGeneralizations());
  }

}
