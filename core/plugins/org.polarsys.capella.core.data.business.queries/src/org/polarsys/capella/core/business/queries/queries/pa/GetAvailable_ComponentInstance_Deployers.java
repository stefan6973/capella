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
package org.polarsys.capella.core.business.queries.queries.pa;

import java.util.ArrayList;
import java.util.List;

import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.capellacore.Type;
import org.polarsys.capella.core.data.cs.AbstractDeploymentLink;
import org.polarsys.capella.core.data.helpers.pa.services.ComponentInstanceExt;
import org.polarsys.capella.core.data.pa.deployment.AbstractPhysicalInstance;
import org.polarsys.capella.core.data.pa.deployment.ComponentInstance;
import org.polarsys.capella.core.data.pa.deployment.TypeDeploymentLink;
import org.polarsys.capella.core.model.helpers.SystemEngineeringExt;
import org.polarsys.capella.core.model.utils.ListExt;

public class GetAvailable_ComponentInstance_Deployers extends AbstractQuery {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> execute(Object input, IQueryContext context) {
		CapellaElement capellaElement = (CapellaElement) input;
		List<CapellaElement> availableElements = getAvailableElements(capellaElement);
		return (List) availableElements;
	}

	/** 
	 */
	public List<CapellaElement> getAvailableElements(CapellaElement element_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
		if (element_p instanceof ComponentInstance) {
			ComponentInstance pc = (ComponentInstance) element_p;
			List<CapellaElement> candidates = getRule_MQRY_PO_Deployers_11(pc);
			for (CapellaElement capellaElement : candidates) {
				if (!ComponentInstanceExt.isDeployedOn((ComponentInstance) capellaElement, pc)) {
					availableElements.add(capellaElement);
				}
			}
		}
		availableElements = ListExt.removeDuplicates(availableElements);
		availableElements.remove(element_p.eContainer());
		availableElements.remove(element_p);
		availableElements.removeAll(getCurrentElements(element_p, false));
		return availableElements;
	}

	/** 
	 * get all the available POs
	 * @param currentPO_pactual element
	 * @return all POs available from the instantiated PhysicalComponent
	 */
	private List<CapellaElement> getRule_MQRY_PO_Deployers_11(ComponentInstance currentPO_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>(1);
		List<AbstractPhysicalInstance> allPOs = SystemEngineeringExt.getAllPhysicalObject(currentPO_p);
		Type instantiatedPC = currentPO_p.getType();
		for (AbstractPhysicalInstance physObj : allPOs) {
			if (physObj instanceof ComponentInstance) {
				for (AbstractDeploymentLink abstDepl : ((ComponentInstance) physObj).getType().getDeploymentLinks()) {
					if (abstDepl.getDeployedElement() == instantiatedPC) {
						availableElements.add(physObj);
					}
				}
			}
		}
		return availableElements;
	}

	/** 
	 * @param onlyGenerated_p
	 */
	public List<CapellaElement> getCurrentElements(CapellaElement element_p, boolean onlyGenerated_p) {
		List<CapellaElement> currentElements = new ArrayList<CapellaElement>();
		if (element_p instanceof ComponentInstance) {
			ComponentInstance pc = (ComponentInstance) element_p;
			List<AbstractDeploymentLink> links = pc.getDeployingLinks();
			for (AbstractDeploymentLink abstractDeployment : links) {
				if (abstractDeployment instanceof TypeDeploymentLink) {
					currentElements.add(abstractDeployment.getLocation());
				}
			}
		}
		return currentElements;
	}

}