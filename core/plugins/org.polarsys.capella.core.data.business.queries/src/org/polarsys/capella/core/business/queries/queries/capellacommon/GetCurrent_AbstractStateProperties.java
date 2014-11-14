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
package org.polarsys.capella.core.business.queries.queries.capellacommon;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EReference;
import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacommon.State;
import org.polarsys.capella.core.data.capellacore.CapellaElement;

public class GetCurrent_AbstractStateProperties extends AbstractQuery {

  @Override
  public List<Object> execute(Object input, IQueryContext context) {
    CapellaElement inputElement = (CapellaElement) input;
    List<CapellaElement> currentElements = new ArrayList<CapellaElement>();

    Object property = context.getValue("theStructuralFeature");
    EReference ref=(EReference) property;
    if (ref.getName()=="doActivity"){
    		currentElements.add((CapellaElement) ((State) input).getDoActivity());
    }
    if (ref.getName()=="entry"){
		currentElements.add((CapellaElement) ((State) input).getEntry());
    }
    if (ref.getName()=="exit"){
		currentElements.add((CapellaElement) ((State) input).getExit());
    }
    return (List) currentElements;
  }

}
