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
package org.polarsys.capella.common.re.re2rpl.create.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.polarsys.capella.common.flexibility.properties.property.AbstractProperty;
import org.polarsys.capella.common.flexibility.properties.schema.ICompoundProperty;
import org.polarsys.capella.common.flexibility.properties.schema.IEditableProperty;
import org.polarsys.capella.common.flexibility.properties.schema.IModifiedProperty;
import org.polarsys.capella.common.flexibility.properties.schema.IProperty;
import org.polarsys.capella.common.flexibility.properties.schema.IPropertyContext;
import org.polarsys.capella.common.re.CatalogElement;
import org.polarsys.capella.common.re.CatalogElementLink;
import org.polarsys.capella.common.re.constants.IReConstants;
import org.polarsys.capella.common.re.handlers.location.LocationHandlerHelper;
import org.polarsys.capella.common.re.handlers.replicable.ReplicableElementHandlerHelper;
import org.polarsys.capella.core.transition.common.capellaHelpers.HashMapSet;
import org.polarsys.capella.core.transition.common.handlers.contextscope.ContextScopeHandlerHelper;
import org.polarsys.kitalpha.transposer.rules.handler.rules.api.IContext;

/**
 *
 */
public class DeleteReplicaContentProperty extends AbstractProperty implements ICompoundProperty, IModifiedProperty, IEditableProperty {

  private static String LINKS = "TEMPORARYLINKS";

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getValue(IPropertyContext context_p) {
    CatalogElement target =
        (CatalogElement) context_p.getCurrentValue(context_p.getProperties().getProperty(IReConstants.PROPERTY__REPLICABLE_ELEMENT__INITIAL_TARGET));

    IContext context = (IContext) context_p.getSource();

    if (context.get(LINKS) != null) {
      return context.get(LINKS);
    }
    Collection<EObject> links = new HashSet<EObject>();

    CatalogElement source =
        (CatalogElement) context_p.getCurrentValue(context_p.getProperties().getProperty(IReConstants.PROPERTY__REPLICABLE_ELEMENT__INITIAL_SOURCE));

    ReplicableElementHandlerHelper.getInstance(context).createInitialReplica(source, target, context);

    Collection<CatalogElementLink> toDelete = new HashSet<CatalogElementLink>();

    if (IReConstants.ENABLE_SUB_INSTANCIATION()) {
      for (CatalogElementLink link : ReplicableElementHandlerHelper.getInstance(context).getAllElementsLinks(target)) {
        toDelete.add(link.getOrigin());
        links.add(link);
      }

    } else {
      for (CatalogElementLink link : ReplicableElementHandlerHelper.getInstance(context).getElementsLinks(target)) {
        toDelete.add(link.getOrigin());
        links.add(link);
        }
      }

    HashMapSet<CatalogElement, CatalogElementLink> toCreate = new HashMapSet<CatalogElement, CatalogElementLink>();

    Collection<CatalogElement> usedSource = new ArrayList<CatalogElement>();
    usedSource.add(source);
    if (IReConstants.ENABLE_SUB_INSTANCIATION()) {
      usedSource.addAll(ReplicableElementHandlerHelper.getInstance(context).getAllUsedReplicableElements(source));
    }

    Collection<CatalogElement> usedTarget = new ArrayList<CatalogElement>();
    usedTarget.add(target);
    if (IReConstants.ENABLE_SUB_INSTANCIATION()) {
      usedTarget.addAll(ReplicableElementHandlerHelper.getInstance(context).getAllUsedReplicableElements(target));
    }

    for (CatalogElement element : usedSource) {
      for (CatalogElementLink link : ReplicableElementHandlerHelper.getInstance(context).getElementsLinks(element)) {
        toCreate.put(element, link);
      }
    }

    for (CatalogElement element : toCreate.keySet()) {
      toCreate.get(element).removeAll(toDelete);

      CatalogElement targetElement = null;
      for (CatalogElement targetElementq : usedTarget) {
        if (targetElementq.getOrigin().equals(element)) {
          targetElement = targetElementq;
          break;
        }
      }

      Collection<CatalogElementLink> targetLinks =
          ReplicableElementHandlerHelper.getInstance(context).createTargetLinks(targetElement, toCreate.get(element), context);
      links.addAll(targetLinks);
    }

    context.put(LINKS, links);
    return links;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IStatus validate(Object newValue_p, IPropertyContext context_p) {
    IContext context = (IContext) context_p.getSource();

    Object useDefault = context_p.getCurrentValue(context_p.getProperties().getProperty(IReConstants.PROPERTY__USE_DEFAULT_LOCATION));
    boolean isUseDefault = !(Boolean.FALSE.equals(useDefault));

    HashSet<CatalogElementLink> links = (HashSet<CatalogElementLink>) newValue_p;
    HashSet<CatalogElementLink> linksInvalid = new HashSet<CatalogElementLink>();

    for (CatalogElementLink link : links) {
      EObject currentLocation = LocationHandlerHelper.getInstance(context).getCurrentLocation(link, context);
      if (currentLocation != null) {
        continue;
      }

      EObject location = LocationHandlerHelper.getInstance(context).getLocation(link, link.getOrigin(), context);
      if (location != null) {
        continue;
      }

      if (isUseDefault) {
        EObject defaultLocation = LocationHandlerHelper.getInstance(context).getDefaultLocation(link, link.getOrigin(), context);
        if (defaultLocation != null) {
          continue;
        }
      }
      linksInvalid.add(link);
    }

    if (!linksInvalid.isEmpty()) {
      return new Status(IStatus.WARNING, IReConstants.PLUGIN_ID, "Some elements need to be stored in the model");

    }
    return Status.OK_STATUS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getType() {
    return Collection.class;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object toType(Object value_p, IPropertyContext context_p) {
    return value_p;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(IPropertyContext context_p) {
    //Nothing yet+
    IContext context = (IContext) context_p.getSource();
    
    HashSet<CatalogElementLink> links = (HashSet) context_p.getCurrentValue(this);
    Object useDefault = context_p.getCurrentValue(context_p.getProperties().getProperty(IReConstants.PROPERTY__USE_DEFAULT_LOCATION));
    boolean isUseDefault = !(Boolean.FALSE.equals(useDefault));

    if (links != null) {

      for (CatalogElementLink link : links) {
        EObject currentLocation = LocationHandlerHelper.getInstance(context).getCurrentLocation(link, context);
        if (currentLocation != null) {
          continue;
        }

        EObject location = LocationHandlerHelper.getInstance(context).getLocation(link, link.getOrigin(), context);
        if (location != null) {
          LocationHandlerHelper.getInstance(context).setCurrentLocation(link, location, context);
          continue;
        }

        if (isUseDefault) {
          EObject defaultLocation = LocationHandlerHelper.getInstance(context).getDefaultLocation(link, link.getOrigin(), context);
          if (defaultLocation != null) {
            LocationHandlerHelper.getInstance(context).setCurrentLocation(link, defaultLocation, context);
            continue;
          }
        }
      }
    }

    CatalogElement replica =
        (CatalogElement) context_p.getCurrentValue(context_p.getProperties().getProperty(IReConstants.PROPERTY__REPLICABLE_ELEMENT__INITIAL_TARGET));

    for (CatalogElementLink link : ReplicableElementHandlerHelper.getInstance(context).getAllElementsLinks(replica)) {
      if (ContextScopeHandlerHelper.getInstance(context).contains(IReConstants.CREATED_LINKS, link, context)) {
        ContextScopeHandlerHelper.getInstance(context).add(IReConstants.CREATED_LINKS_TO_KEEP, link, context);
      }
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getRelatedProperties() {
    return new String[] { IReConstants.PROPERTY__REPLICABLE_ELEMENT__INITIAL_TARGET, IReConstants.PROPERTY__USE_DEFAULT_LOCATION,
                         IReConstants.PROPERTY__LOCATION_TARGET };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updatedValue(IProperty property_p, IPropertyContext context_p) {
    if (IReConstants.PROPERTY__REPLICABLE_ELEMENT__INITIAL_TARGET.equals(property_p.getId())) {
      IContext context = (IContext) context_p.getSource();
      context.put(LINKS, null);
      ReplicableElementHandlerHelper.getInstance(context).cleanVirtualLinks(context);
      LocationHandlerHelper.getInstance(context).cleanLocations(context);

    } else if (IReConstants.PROPERTY__REPLICABLE_ELEMENT__INITIAL_SOURCE.equals(property_p.getId())) {
      IContext context = (IContext) context_p.getSource();
      context.put(LINKS, null);
      ReplicableElementHandlerHelper.getInstance(context).cleanVirtualLinks(context);
      LocationHandlerHelper.getInstance(context).cleanLocations(context);
    }
    //Nothing here
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModified(IPropertyContext context_p) {
    return true;
  }

}
