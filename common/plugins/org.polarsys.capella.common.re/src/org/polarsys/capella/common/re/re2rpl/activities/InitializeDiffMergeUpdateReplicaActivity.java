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
package org.polarsys.capella.common.re.re2rpl.activities;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.diffmerge.api.scopes.IEditableModelScope;
import org.eclipse.emf.ecore.EObject;
import org.polarsys.capella.common.re.CatalogElement;
import org.polarsys.capella.common.re.constants.IReConstants;
import org.polarsys.capella.common.re.handlers.replicable.ReplicableElementHandlerHelper;
import org.polarsys.capella.common.re.handlers.traceability.ReConfiguration;
import org.polarsys.capella.common.re.merge.scope.ReSourceScope;
import org.polarsys.capella.common.re.merge.scope.ReTargetScope;
import org.polarsys.capella.core.transition.common.activities.InitializeDiffMergeFromTransformationActivity;
import org.polarsys.capella.core.transition.common.constants.ITransitionConstants;
import org.polarsys.capella.core.transition.common.handlers.IHandler;
import org.polarsys.capella.core.transition.common.handlers.contextscope.ContextScopeHandlerHelper;
import org.polarsys.capella.core.transition.common.handlers.filter.FilteringDifferencesHandlerHelper;
import org.polarsys.capella.core.transition.common.handlers.options.OptionsHandlerHelper;
import org.polarsys.capella.core.transition.common.handlers.traceability.CompoundTraceabilityHandler;
import org.polarsys.capella.core.transition.common.handlers.traceability.ITraceabilityHandler;
import org.polarsys.capella.core.transition.common.handlers.traceability.config.ITraceabilityConfiguration;
import org.polarsys.capella.core.transition.common.merge.scope.IModelScopeFilter;
import org.polarsys.capella.core.transition.common.merge.scope.PartialRootedModelScope;
import org.polarsys.kitalpha.cadence.core.api.parameter.ActivityParameters;
import org.polarsys.kitalpha.transposer.api.ITransposerWorkflow;
import org.polarsys.kitalpha.transposer.rules.handler.rules.api.IContext;

/**
 * 
 */
public class InitializeDiffMergeUpdateReplicaActivity extends InitializeDiffMergeFromTransformationActivity {

  public static final String ID = InitializeDiffMergeUpdateReplicaActivity.class.getCanonicalName();

  @Override
  public IStatus _run(ActivityParameters activityParams_p) {
    IContext context = (IContext) activityParams_p.getParameter(ITransposerWorkflow.TRANSPOSER_CONTEXT).getValue();

    FilteringDifferencesHandlerHelper.getInstance(context).dispose(context);
    ContextScopeHandlerHelper.getInstance(context).clear(IReConstants.SOURCE__ADDED_ELEMENTS, context);
    ContextScopeHandlerHelper.getInstance(context).clear(IReConstants.TARGET__ADDED_ELEMENTS, context);

    LinkedList<CatalogElement> elements = ReplicableElementHandlerHelper.getInstance(context).getListSources(context);
    if (elements.isEmpty()) {
      return Status.OK_STATUS;
    }

    CatalogElement esource = elements.removeFirst();

    Collection<CatalogElement> targets = new HashSet<CatalogElement>();
    CatalogElement etarget = ReplicableElementHandlerHelper.getInstance(context).getInitialTarget(context);
    targets.add(etarget);
    targets.addAll(ReplicableElementHandlerHelper.getInstance(context).getAllUsedReplicableElements(etarget));

    //Order is important, if we have hierarchical REC defined as : A, A1, A2, A11, A12, A21, A22
    //we will trigger diff-merge successively on A, A1, A11, A12, A1, A2, A21, A22, A2, A

    Collection<CatalogElement> usedReplicable = ReplicableElementHandlerHelper.getInstance(context).getUsedReplicableElements(esource);
    usedReplicable.remove(esource);
    if (!usedReplicable.isEmpty() && !(ReplicableElementHandlerHelper.getInstance(context).getListSourcesVisited(context).contains(esource))) {
      if (IReConstants.ENABLE_SUB_INSTANCIATION()) {
        //loop on children
        elements.addFirst(esource);
        for (CatalogElement aO : usedReplicable) {
          elements.addFirst(aO);
        }
      }
      ReplicableElementHandlerHelper.getInstance(context).getListSourcesVisited(context).add(esource);
    }

    for (CatalogElement target : targets) {
      if (esource.equals(target.getOrigin())) {
        ReplicableElementHandlerHelper.getInstance(context).setTarget(context, target);
      }
    }

    ReplicableElementHandlerHelper.getInstance(context).setSource(context, esource);
    return super._run(activityParams_p);
  }

  /**
   * @param context_p
   * @param activityParams_p
   * @return
   */
  @Override
  protected IStatus initializeReferenceScope(IContext context_p, ActivityParameters activityParams_p) {

    CatalogElement source = ReplicableElementHandlerHelper.getInstance(context_p).getSource(context_p);
    CatalogElement target = ReplicableElementHandlerHelper.getInstance(context_p).getTarget(context_p);

    //Mergeable scope is only elements directly linked to source replicable element. Unmodifiable elements, shared elements are computed
    //If replicable element is into a writeable area, we could consider to let computed scope to be merge-scope, but some elements can 
    //be linked to source replicable element, adding links to them.

    Collection<EObject> scopeElements = new HashSet<EObject>();
    //Scope is computed, we put it into Merge Scope
    scopeElements =
        OptionsHandlerHelper.getInstance(context_p).getCollectionValue(context_p, (String) context_p.get(ITransitionConstants.OPTIONS_SCOPE),
            IReConstants.PROPERTY__MERGE_SOURCE_SCOPE, (Collection) Collections.emptyList());

    //Ensure unwanted elements not in scope!
    scopeElements.remove(source);
    scopeElements.remove(target);

    //Scope is computed, we put it into Merge Scope
    ITraceabilityHandler handler = (ITraceabilityHandler) context_p.get(ITransitionConstants.TRACEABILITY_SOURCE_MERGE_HANDLER);
    IEditableModelScope sourceScope = new ReSourceScope(source, handler, scopeElements, context_p);
    context_p.put(ITransitionConstants.MERGE_REFERENCE_SCOPE, sourceScope);
    ((PartialRootedModelScope) sourceScope).build(getReferenceFilter(context_p));

    return Status.OK_STATUS;
  }

  /**
   * @param context_p
   * @param activityParams_p
   * @return
   */
  @Override
  protected IStatus initializeTargetScope(IContext context_p, ActivityParameters activityParams_p) {

    CatalogElement source = ReplicableElementHandlerHelper.getInstance(context_p).getSource(context_p);
    CatalogElement target = ReplicableElementHandlerHelper.getInstance(context_p).getTarget(context_p);

    //Target is the ReplicableElement
    Collection<EObject> scopeElements = new HashSet<EObject>();

    //Scope is computed, we put it into Merge Scope
    scopeElements =
        OptionsHandlerHelper.getInstance(context_p).getCollectionValue(context_p, (String) context_p.get(ITransitionConstants.OPTIONS_SCOPE),
            IReConstants.PROPERTY__MERGE_TARGET_SCOPE, (Collection) Collections.emptyList());

    //Ensure unwanted elements not in scope!
    scopeElements.remove(source);
    scopeElements.remove(target);

    ITraceabilityHandler handler = (ITraceabilityHandler) context_p.get(ITransitionConstants.TRACEABILITY_TARGET_MERGE_HANDLER);
    IEditableModelScope targetScope = new ReTargetScope(target, handler, scopeElements, context_p);
    context_p.put(ITransitionConstants.MERGE_TARGET_SCOPE, targetScope);
    ((PartialRootedModelScope) targetScope).build(getTargetFilter(context_p));

    return Status.OK_STATUS;
  }

  @Override
  protected IStatus initializeTraceabilitySourceHandler(IContext context_p, ActivityParameters activityParams_p) {
    return super.initializeTraceabilitySourceHandler(context_p, activityParams_p);
  }

  @Override
  protected IStatus initializeTraceabilityTargetHandler(IContext context_p, ActivityParameters activityParams_p) {
    return super.initializeTraceabilityTargetHandler(context_p, activityParams_p);
  }

  /**
   * Create default traceability handler for source of diffMerge
   */
  @Override
  protected IHandler createDefaultTraceabilitySourceHandler(IContext context_p) {
    CatalogElement source = ReplicableElementHandlerHelper.getInstance(context_p).getSource(context_p);
    CatalogElement target = ReplicableElementHandlerHelper.getInstance(context_p).getTarget(context_p);

    ITraceabilityConfiguration configuration = new ReConfiguration(source);
    return new CompoundTraceabilityHandler(configuration);
  }

  /**
   * Create default traceability handler for target of diffMerge
   */
  @Override
  protected IHandler createDefaultTraceabilityTargetHandler(IContext context_p) {
    CatalogElement source = ReplicableElementHandlerHelper.getInstance(context_p).getSource(context_p);
    CatalogElement target = ReplicableElementHandlerHelper.getInstance(context_p).getTarget(context_p);

    ITraceabilityConfiguration configuration = new ReConfiguration(source, target);
    return new CompoundTraceabilityHandler(configuration);
  }

  @Override
  protected IModelScopeFilter getReferenceFilter(final IContext context_p) {
    return new IModelScopeFilter() {
      public boolean accepts(EObject element_p) {
        return ((ReSourceScope) (context_p.get(ITransitionConstants.MERGE_REFERENCE_SCOPE))).getInitialElements().contains(element_p);
      }
    };
  }

  @Override
  protected IModelScopeFilter getTargetFilter(final IContext context_p) {
    return new IModelScopeFilter() {
      public boolean accepts(EObject element_p) {
        return ((ReSourceScope) (context_p.get(ITransitionConstants.MERGE_TARGET_SCOPE))).getInitialElements().contains(element_p);
      }
    };
  }
}
