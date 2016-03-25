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

package org.polarsys.capella.core.transition.system.rules.information.datavalue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.polarsys.capella.core.data.information.datavalue.DatavaluePackage;
import org.polarsys.capella.core.data.information.datavalue.StringReference;
import org.polarsys.capella.core.transition.common.handlers.attachment.AttachmentHelper;
import org.polarsys.kitalpha.transposer.rules.handler.rules.api.IContext;
import org.polarsys.kitalpha.transposer.rules.handler.rules.api.IPremise;

public class StringReferenceRule extends DataValueRule {

  public StringReferenceRule() {
    super();
  }

  @Override
  protected EClass getSourceType() {
    return DatavaluePackage.Literals.STRING_REFERENCE;
  }

  @Override
  protected void retrieveGoDeep(EObject source, List<EObject> result, IContext context) {
    super.retrieveGoDeep(source, result, context);
    StringReference element = (StringReference) source;
    result.add(element.getReferencedProperty());
    result.add(element.getReferencedValue());
  }

  @Override
  protected void attachRelated(EObject element, EObject result, IContext context) {
    super.attachRelated(element, result, context);
    AttachmentHelper.getInstance(context).attachTracedElements(element, result, DatavaluePackage.Literals.STRING_REFERENCE__REFERENCED_PROPERTY,
        context);
    AttachmentHelper.getInstance(context).attachTracedElements(element, result, DatavaluePackage.Literals.STRING_REFERENCE__REFERENCED_VALUE, context);
  }

  @Override
  protected void premicesRelated(EObject element, ArrayList<IPremise> needed) {
    super.premicesRelated(element, needed);
    needed.addAll(createDefaultPrecedencePremices(element, DatavaluePackage.Literals.STRING_REFERENCE__REFERENCED_PROPERTY));
    needed.addAll(createDefaultPrecedencePremices(element, DatavaluePackage.Literals.STRING_REFERENCE__REFERENCED_VALUE));
  }
}
