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
package org.polarsys.capella.core.business.queries.queries.cs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.polarsys.capella.common.data.modellingcore.AbstractTrace;
import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.cs.AbstractActor;
import org.polarsys.capella.core.data.cs.PhysicalPort;
import org.polarsys.capella.core.data.ctx.System;
import org.polarsys.capella.core.data.fa.ComponentPortAllocation;
import org.polarsys.capella.core.data.information.Port;
import org.polarsys.capella.core.data.la.LogicalComponent;
import org.polarsys.capella.core.data.pa.PhysicalActor;
import org.polarsys.capella.core.data.pa.PhysicalComponent;
import org.polarsys.capella.core.model.helpers.LogicalComponentExt;
import org.polarsys.capella.core.model.utils.ListExt;

public class GetAvailable_PhysicalPort_AllocatedComponentPorts extends AbstractQuery {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> execute(Object input, IQueryContext context) {
		CapellaElement capellaElement = (CapellaElement) input;
		List<CapellaElement> availableElements = getAvailableElements(capellaElement);
		return (List) availableElements;
	}

	/** 
	 * {@inheritDoc}
	 */
	public List<CapellaElement> getAvailableElements(CapellaElement element_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
		if (element_p instanceof PhysicalPort) {
			List<CapellaElement> currentElements = getCurrentElements(element_p, false);
			for (EObject port : getRule_MQRY_Port_AllocatedPorts_11((Port) element_p)) {
				if (!currentElements.contains(port)) {
					availableElements.add((CapellaElement) port);
				}
			}
		}
		availableElements.remove(element_p);
		return availableElements;
	}

	/** 
	 * {@inheritDoc}
	 */
	public List<CapellaElement> getCurrentElements(CapellaElement element_p, boolean onlyGenerated_p) {
		List<CapellaElement> currentElements = new ArrayList<CapellaElement>();
		if (element_p instanceof PhysicalPort) {
			PhysicalPort elt = (PhysicalPort) element_p;
			for (AbstractTrace trace : elt.getOutgoingTraces()) {
				if (trace instanceof ComponentPortAllocation) {
					if (((ComponentPortAllocation) trace).getAllocatedPort() != null) {
						currentElements.add(((ComponentPortAllocation) trace).getAllocatedPort());
					}
				}
			}
			currentElements = ListExt.removeDuplicates(currentElements);
			currentElements.remove(elt);
		}
		return currentElements;
	}

	/** 
	 */
	protected List<CapellaElement> getRule_MQRY_Port_AllocatedPorts_11(Port element_p) {
		List<CapellaElement> allPorts = new ArrayList<CapellaElement>();
		EObject ownerObj = element_p.eContainer();
		if (ownerObj instanceof System) {
			allPorts.addAll(((System) ownerObj).getContainedComponentPorts());
		} else if (ownerObj instanceof LogicalComponent) {
			for (LogicalComponent lc : LogicalComponentExt.getAllSubComponents((LogicalComponent) ownerObj)) {
				allPorts.addAll(lc.getContainedComponentPorts());
			}
		} else if (ownerObj instanceof PhysicalComponent) {
			for (PhysicalComponent deployedCpnt : ((PhysicalComponent) ownerObj).getDeployedPhysicalComponents()) {
				allPorts.addAll(deployedCpnt.getContainedComponentPorts());
			}
		} else if (ownerObj instanceof PhysicalActor) {
			for (PhysicalComponent deployedCpnt : ((PhysicalActor) ownerObj).getDeployedPhysicalComponents()) {
				allPorts.addAll(deployedCpnt.getContainedComponentPorts());
			}
		} else if (ownerObj instanceof AbstractActor) {
			allPorts.addAll(((AbstractActor) ownerObj).getContainedComponentPorts());
		}
		allPorts = ListExt.removeDuplicates(allPorts);
		return allPorts;
	}

}