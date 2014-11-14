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
package org.polarsys.capella.core.business.queries.queries.ctx;

import java.util.ArrayList;
import java.util.List;

import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.capellamodeller.SystemEngineering;
import org.polarsys.capella.core.data.ctx.Actor;
import org.polarsys.capella.core.data.helpers.ctx.services.ActorPkgExt;
import org.polarsys.capella.core.model.helpers.InterfacePkgExt;
import org.polarsys.capella.core.model.helpers.SystemEngineeringExt;
import org.polarsys.capella.core.model.helpers.query.CapellaQueries;

public class GetAvailable_Actor_ImplementedInterfaces extends AbstractQuery {

  @Override
  public List<Object> execute(Object input, IQueryContext context) {
    CapellaElement capellaElement = (CapellaElement) input;
    List<CapellaElement> availableElements = getAvailableElements(capellaElement);
    return (List) availableElements;
  }

  /**
   * <p>
   * Gets all the interfaces contained in the interface package and all of its sub packages of the system engineering & shared package.
   * </p>
   * <p>
   * Except those that are implemented by the current actor.
   * </p>
   * <p>
   * Refer MQRY_Actor_ImplInterfaces_1
   * </p>
   * @see org.polarsys.capella.core.business.queries.capellacore.core.business.queries.IBusinessQuery#getAvailableElements(org.polarsys.capella.core.common.model.CapellaElement)
   */
  public List<CapellaElement> getAvailableElements(CapellaElement element_p) {
    SystemEngineering systemEngineering = CapellaQueries.getInstance().getRootQueries().getSystemEngineering(element_p);
    List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
    if (null == systemEngineering) {
      return availableElements;
    }
    if (element_p instanceof Actor) {
      Actor currentActor = (Actor) element_p;
      availableElements.addAll(getRule_MQRY_Actor_ImplInterfaces11(currentActor, systemEngineering));
      availableElements.addAll(getRule_MQRY_Actor_ImplInterfaces12(currentActor, systemEngineering));
    }
    return availableElements;
  }

  /**
   * <p>
   * Gets all the interfaces contained in the interface package and all of its sub packages of the SystemEngineering Package.
   * </p>
   * <p>
   * Except the interfaces that are already implemented by the current actor
   * </p>
   * <p>
   * Refer MQRY_Actor_ImplInterfaces_11
   * </p>
   * @param currentActor_pthe current {@link Actor}
   * @param systemEngineering_pthe {@link SystemEngineering}
   * @return list of interfaces
   */
  private List<CapellaElement> getRule_MQRY_Actor_ImplInterfaces11(Actor currentActor_p, SystemEngineering systemEngineering_p) {
    List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
    availableElements.addAll(InterfacePkgExt.getAllInterfaces(SystemEngineeringExt.getOwnedSystemAnalysis(systemEngineering_p).getOwnedInterfacePkg()));

    List<Actor> allActors = ActorPkgExt.getAllActors(SystemEngineeringExt.getOwnedSystemAnalysis(systemEngineering_p).getOwnedActorPkg());
    for (Actor actor : allActors) {
      availableElements.addAll(InterfacePkgExt.getAllInterfaces(actor.getOwnedInterfacePkg()));
    }
    return availableElements;
  }

  /**
   * <p>
   * Gets all the interfaces contained in the interface package and all of its sub packages of the Shared Package.
   * </p>
   * <p>
   * Except the interfaces that are already implemented by the current actor
   * </p>
   * <p>
   * Refer MQRY_Actor_ImplInterfaces_12
   * </p>
   * @param currentActor_pthe current {@link Actor}
   * @param systemEngineering_pthe {@link SystemEngineering}
   * @return list of interfaces
   */
  private List<CapellaElement> getRule_MQRY_Actor_ImplInterfaces12(Actor currentActor_p, SystemEngineering systemEngineering_p) {
    List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
    return availableElements;
  }

}
