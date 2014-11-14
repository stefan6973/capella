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
package org.polarsys.capella.core.projection.exchanges;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.polarsys.capella.core.data.cs.AbstractDeploymentLink;
import org.polarsys.capella.core.data.cs.Component;
import org.polarsys.capella.core.data.cs.CsFactory;
import org.polarsys.capella.core.data.cs.DeployableElement;
import org.polarsys.capella.core.data.cs.DeploymentTarget;
import org.polarsys.capella.core.data.cs.Part;
import org.polarsys.capella.core.data.cs.PhysicalLink;
import org.polarsys.capella.core.data.cs.PhysicalPort;
import org.polarsys.capella.core.data.fa.ComponentExchange;
import org.polarsys.capella.core.data.fa.ComponentExchangeAllocation;
import org.polarsys.capella.core.data.fa.ComponentExchangeKind;
import org.polarsys.capella.core.data.fa.ComponentPort;
import org.polarsys.capella.core.data.fa.ComponentPortAllocation;
import org.polarsys.capella.core.data.fa.FaFactory;
import org.polarsys.capella.core.data.fa.FunctionalExchange;
import org.polarsys.capella.core.data.helpers.fa.services.FunctionalExt;
import org.polarsys.capella.core.data.information.AbstractEventOperation;
import org.polarsys.capella.core.data.information.Partition;
import org.polarsys.capella.core.data.capellacore.Type;
import org.polarsys.capella.core.data.pa.AbstractPhysicalComponent;
import org.polarsys.capella.core.data.pa.PhysicalActor;
import org.polarsys.capella.core.data.pa.PhysicalComponent;
import org.polarsys.capella.core.data.pa.PhysicalComponentNature;
import org.polarsys.capella.core.data.pa.deployment.PartDeploymentLink;
import org.polarsys.capella.core.model.helpers.ComponentExt;
import org.polarsys.capella.core.model.helpers.CapellaElementExt;
import org.polarsys.capella.core.model.helpers.PartExt;
import org.polarsys.capella.core.model.helpers.PhysicalLinkExt;
import org.polarsys.capella.core.model.helpers.PortExt;
import org.polarsys.capella.common.data.modellingcore.InformationsExchanger;
import org.polarsys.capella.common.data.modellingcore.TraceableElement;
import org.polarsys.capella.common.platform.sirius.ted.SemanticEditingDomainFactory.SemanticEditingDomain;

/**
 * This class is the <code>IExchangesCreator</code> implementation specific to node physical components.<br>
 * This implementation creates physical links.
 */
public class NodePhysicalComponentExchangesCreator extends DefaultExchangesCreator {

  private Part _part = null;

  /**
   * Constructor
   * @param component_p
   */
  public NodePhysicalComponentExchangesCreator(Component component_p, Part part_p) {
    super(component_p);
    if (null != part_p) {
      _part = part_p;
    }
  }

  /**
   * This implementation creates physical links.
   * @see org.polarsys.capella.core.projection.commands.utils.DefaultExchangesCreator#createExchanges()
   */
  @Override
  public void createExchanges() {
    if (_component instanceof AbstractPhysicalComponent) {
      // Casts the component as a physical component
      AbstractPhysicalComponent node = (AbstractPhysicalComponent) _component;
      createExchangesForDeployedPhysicalComponents(node);
      // Creates the exchanges for directly allocated functions
      super.createExchanges();
    }
  }

  /**
   * Returns whether a component which will be a bound of a created exchange is valid or not
   * @param component_p
   * @return
   */
  @Override
  protected boolean isValidBound(Component component_p) {
    if (component_p instanceof AbstractPhysicalComponent) {
      AbstractPhysicalComponent component = (AbstractPhysicalComponent) component_p;
      PhysicalComponentNature nature = component.getNature();
      if ((component instanceof PhysicalComponent) && (nature == PhysicalComponentNature.NODE)) {
        return true;

      } else if (component instanceof PhysicalActor) {
        return true;
      }
    }
    return false;
  }

  /**
   * @see org.polarsys.capella.core.projection.exchanges.DefaultExchangesCreator#isValidCreation(org.polarsys.capella.core.data.fa.FunctionalExchange, org.polarsys.capella.core.data.cs.Component, org.polarsys.capella.core.data.cs.Component)
   */
  @Override
  protected boolean isValidCreation(AbstractEventOperation fe_p, Component component_p, Component allocating_p) {
    return isValidBound(component_p) && isValidBound(allocating_p);
  }

  /**
   * Creates the exchanges related to exchanges between deployed physical components.
   * @param node_p the node physical component from which the search of deployed physical component will be done
   */
  protected void createExchangesForDeployedPhysicalComponents(AbstractPhysicalComponent node_p) {

    // Gets the deployments of the node
    EList<AbstractDeploymentLink> deployments = _part.getDeploymentLinks();

    for (AbstractDeploymentLink deployment : deployments) {
      if (deployment instanceof PartDeploymentLink) {
        PartDeploymentLink deploymentLink = (PartDeploymentLink) deployment;
        DeployableElement deployedElement = deploymentLink.getDeployedElement();
        if ((deployedElement != null) && (deployedElement instanceof Part)) {
          Part part = (Part) deployedElement;
          Type type = part.getType();
          if (null != type) {
            createExchangesFromDeployedElement(node_p, type);
          }
        } else if ((deployedElement != null) && (deployedElement instanceof AbstractPhysicalComponent)) {
          createExchangesFromDeployedElement(node_p, (AbstractPhysicalComponent) deployedElement);
        }
      }
    }
  }

  /**
   * @param node_p
   * @param type
   */
  private void createExchangesFromDeployedElement(AbstractPhysicalComponent node_p, Type type) {
    if ((type != null) && (type instanceof AbstractPhysicalComponent)) {
      // Process each deployed PC
      // DeployableElement deployedElement =
      // deployment.getDeployedElement();
      AbstractPhysicalComponent deployedPhysicalComponent = (AbstractPhysicalComponent) type;
      // This reference will allows to handle the processed connections
      for (ComponentPort port : ComponentExt.getOwnedComponentPort(deployedPhysicalComponent)) {
        // Process the flow ports of the deployed PC
        // filter inValid port
        //        if (PortExt.isOut(port)) {
        // get all the connection of the port
        for (ComponentExchange connection : port.getComponentExchanges()) {
          // filter delegation and unSet type of connection
          if ((connection.getKind() != ComponentExchangeKind.DELEGATION) && (connection.getKind() != ComponentExchangeKind.UNSET)) {
            // proceed only if port is a source of current
            // Connection
            //              if (connection.getSource().equals(port)) {
            // check if physicalLink creation is necessary
            if (!doesNodeAlreadyHaveAPhysicalLinkForComponentExchange(node_p, connection)) {
              // get the opposite port [which could be
              // source or target of the Connection]
              InformationsExchanger target = FunctionalExt.getOtherBound(connection, port);
              if ((target != null) && (target instanceof ComponentPort)) {
                // get the container of the target port
                EObject container = target.eContainer();
                // find the target Node
                // [PhysicalComponent]
                if (container instanceof AbstractPhysicalComponent) {

                  //For all parts, find the deploying component
                  for (Partition partition : ((AbstractPhysicalComponent) container).getRepresentingPartitions()) {
                    if (partition instanceof Part) {
                      for (DeploymentTarget deploying : PartExt.getDeployingElements((Part) partition)) {
                        if (deploying instanceof Part) {
                          Part deployingPart = (Part) deploying;
                          if ((deployingPart.getAbstractType() != null) && (deployingPart.getAbstractType() instanceof AbstractPhysicalComponent)) {
                            AbstractPhysicalComponent typeDeploying = (AbstractPhysicalComponent) deployingPart.getAbstractType();

                            //Create an exchange if there is no connection created
                            //TODO In multipart, create physical links with part related, not type
                            if (isValidCreation(connection, node_p, typeDeploying) && !doesNodeAlreadyHaveAPhysicalLinkForComponentExchange(node_p, connection)) {
                              if (connection.getSource().equals(port)) {
                                doCreateExchange(connection, node_p, typeDeploying);
                              } else {
                                doCreateExchange(connection, typeDeploying, node_p);
                              }
                            }
                          }
                        }
                      }
                    }
                  }

                }
              }
            }
            //              }
          }
        }
        //        }
      }

    }
  }

  /**
   * This implementation creates physical links related to the given functional exchange.
   * @see org.polarsys.capella.core.projection.commands.utils.DefaultExchangesCreator#doCreatePhysicalLink(org.polarsys.capella.core.data.fa.FunctionalExchange,
   *      org.polarsys.capella.core.data.cs.Component, org.polarsys.capella.core.data.cs.Component)
   */
  @Override
  protected void doCreateExchange(FunctionalExchange functionalExchange_p, Component exchangeOutput_p, Component exchangeInput_p) {
    PhysicalLink physicalLink = CsFactory.eINSTANCE.createPhysicalLink(functionalExchange_p.getLabel());
    PhysicalPort outP = CsFactory.eINSTANCE.createPhysicalPort(functionalExchange_p.getSource().getName());
    PhysicalPort inP = CsFactory.eINSTANCE.createPhysicalPort(functionalExchange_p.getTarget().getName());
    physicalLink.getLinkEnds().add(outP);
    physicalLink.getLinkEnds().add(inP);
    exchangeInput_p.getOwnedFeatures().add(inP);
    exchangeOutput_p.getOwnedFeatures().add(outP);
    PhysicalLinkExt.attachToDefaultContainer(physicalLink);

    // Creates the ports allocation
    PortExt.attachPort(outP, functionalExchange_p.getSource());
    PortExt.attachPort(inP, functionalExchange_p.getTarget());
  }

  /**
   * Create a physical link corresponding to the given component exchange, between the given components
   * @param componentExchange_p the source component exchange
   * @param exchangeOutput_p the output component
   * @param exchangeInput_p the input component
   */
  protected void doCreateExchange(ComponentExchange componentExchange_p, Component exchangeOutput_p, Component exchangeInput_p) {
    // Precondition:
    if (exchangeOutput_p == exchangeInput_p) {
      // Not necessary to create a physical link for exchanges inside the
      // same container.
      return;
    }
    PhysicalLink physicalLink = CsFactory.eINSTANCE.createPhysicalLink(componentExchange_p.getLabel());
    PhysicalPort outP = CsFactory.eINSTANCE.createPhysicalPort(componentExchange_p.getSource().getLabel());
    PhysicalPort inP = CsFactory.eINSTANCE.createPhysicalPort(componentExchange_p.getTarget().getLabel());
    physicalLink.getLinkEnds().add(outP);
    physicalLink.getLinkEnds().add(inP);

    exchangeInput_p.getOwnedFeatures().add(inP);
    exchangeOutput_p.getOwnedFeatures().add(outP);
    CapellaElementExt.creationService(inP);
    CapellaElementExt.creationService(outP);

    PhysicalLinkExt.attachToDefaultContainer(physicalLink);
    CapellaElementExt.creationService(physicalLink);

    // Creates the exchange allocation
    ComponentExchangeAllocation cea = FaFactory.eINSTANCE.createComponentExchangeAllocation();
    cea.setSourceElement(physicalLink);
    cea.setTargetElement(componentExchange_p);
    physicalLink.getOwnedComponentExchangeAllocations().add(cea);
    CapellaElementExt.creationService(cea);

    // source side delegation
    InformationsExchanger target = componentExchange_p.getTarget();
    createComponentPortAllocation(target, inP);

    // target side Delegation
    InformationsExchanger source = componentExchange_p.getSource();
    createComponentPortAllocation(source, outP);

  }

  /**
   * @param informationExchange_p
   * @param physicalPort_p
   * @param connection_p
   */
  private ComponentPortAllocation createComponentPortAllocation(InformationsExchanger informationExchange_p, PhysicalPort physicalPort_p) {
    ComponentPortAllocation allocation = FaFactory.eINSTANCE.createComponentPortAllocation();
    allocation.setSourceElement(physicalPort_p);
    allocation.setTargetElement((TraceableElement) informationExchange_p);
    physicalPort_p.getOwnedComponentPortAllocations().add(allocation);
    CapellaElementExt.creationService(allocation);
    return allocation;
  }

  /**
   * This method allows to know if the given component exchange has already been allocated to a physical link linked to the given physical component.
   * @param physicalComponent_p the physical component
   * @param componentExchange_p the component exchange
   * @return true if its has already been allocated, false otherwise
   */
  protected boolean doesNodeAlreadyHaveAPhysicalLinkForComponentExchange(AbstractPhysicalComponent physicalComponent_p, ComponentExchange componentExchange_p) {
    boolean result = false;
    // Get the semantic editing domain to access the cross referencer.
    SemanticEditingDomain editingDomain = (SemanticEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(componentExchange_p);
    // Get the cross referencer.
    ECrossReferenceAdapter crossReferencer = editingDomain.getCrossReferencer();
    // Search inverses relations on given component exchange.
    Collection<Setting> inverseReferences = crossReferencer.getInverseReferences(componentExchange_p, true);
    for (Setting setting : inverseReferences) {
      // Search for a relation targeting the ComponentExchangeAllocation metaclass.
      if (setting.getEObject() instanceof ComponentExchangeAllocation) {
        result = true;
        break;
      }
    }
    return result;
  }
}