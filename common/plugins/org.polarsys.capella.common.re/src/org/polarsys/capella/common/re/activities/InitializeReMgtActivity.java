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
package org.polarsys.capella.common.re.activities;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.polarsys.capella.common.re.constants.IReConstants;
import org.polarsys.capella.common.re.handlers.replicable.ReplicableElementHandlerHelper;
import org.polarsys.capella.common.re.handlers.traceability.MatchConfiguration;
import org.polarsys.capella.core.transition.common.activities.AbstractActivity;
import org.polarsys.capella.core.transition.common.constants.ITransitionConstants;
import org.polarsys.capella.core.transition.common.handlers.IHandler;
import org.polarsys.capella.core.transition.common.handlers.contextscope.ContextScopeHandlerHelper;
import org.polarsys.capella.core.transition.common.handlers.contextscope.IContextScopeHandler;
import org.polarsys.capella.core.transition.common.handlers.scope.ScopeHandlerHelper;
import org.polarsys.capella.core.transition.common.handlers.traceability.CompoundTraceabilityHandler;
import org.polarsys.kitalpha.cadence.core.api.parameter.ActivityParameters;
import org.polarsys.kitalpha.transposer.api.ITransposerWorkflow;
import org.polarsys.kitalpha.transposer.rules.handler.rules.api.IContext;

/**
 *
 */
public class InitializeReMgtActivity extends AbstractActivity {

  public static final String ID = InitializeReMgtActivity.class.getCanonicalName();

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus _run(ActivityParameters activityParams_p) {
    IContext context = (IContext) activityParams_p.getParameter(ITransposerWorkflow.TRANSPOSER_CONTEXT).getValue();
    IStatus status = Status.OK_STATUS;

    context.put(IReConstants.COMMAND__CURRENT_VALUE, activityParams_p.getParameter(IReConstants.COMMAND__CURRENT_VALUE).getValue());

    status = initializeReplicableElements(context, activityParams_p);
    if (!checkStatus(status)) {
      return status;
    }

    initializeTraceabilityAttachmentHandler(context, activityParams_p);

    //Compute scope and additional elements
    IContextScopeHandler scopeHandler = ContextScopeHandlerHelper.getInstance(context);
    Collection<EObject> scopeElements = new HashSet<EObject>();
    scopeHandler.clear(ITransitionConstants.INITIAL_SOURCE_SCOPE, context);
    scopeHandler.clear(ITransitionConstants.SOURCE_SCOPE, context);
    scopeHandler.addAll(ITransitionConstants.INITIAL_SOURCE_SCOPE, scopeElements, context);
    scopeHandler.addAll(ITransitionConstants.SOURCE_SCOPE, scopeElements, context);

    status = ScopeHandlerHelper.getInstance(context).computeScope(scopeElements, context);
    if (!checkStatus(status)) {
      return status;
    }

    ReplicableElementHandlerHelper.getInstance(context).getListSources(context)
        .add(ReplicableElementHandlerHelper.getInstance(context).getInitialSource(context));

    return Status.OK_STATUS;
  }

  /**
   * Initialize the transformation traceability handler and set it into context via TRANSFORMATION_HANDLER
   * @param context_p
   * @param activityParams_p
   * @return
   */
  protected IStatus initializeTraceabilityAttachmentHandler(IContext context_p, ActivityParameters activityParams_p) {
    IHandler handler = loadHandlerFromParameters(IReConstants.TRACEABILITY_ATTACHMENT_HANDLER, activityParams_p);
    if (handler == null) {
      handler = createDefaultTraceabilityTransformationHandler();
    }
    context_p.put(IReConstants.TRACEABILITY_ATTACHMENT_HANDLER, handler);
    context_p.put(ITransitionConstants.TRACEABILITY_HANDLER, handler);
    handler.init(context_p);
    return Status.OK_STATUS;
  }

  /**
   * Create default transformation traceability handler for common transition
   * @return
   */
  protected IHandler createDefaultTraceabilityTransformationHandler() {
    return new CompoundTraceabilityHandler(new MatchConfiguration());
  }

  /**
   * Should compute scope
   * ScopeHandlerHelper.getInstance(context_p).getScope should not be null
   * @param context_p
   */
  protected IStatus initializeReplicableElements(IContext context_p, ActivityParameters activityParams_p) {
    Collection<EObject> selection = (Collection<EObject>) context_p.get(ITransitionConstants.TRANSITION_SOURCES);
    context_p.put(ITransitionConstants.SCOPE_SOURCES, selection);
    return Status.OK_STATUS;
  }
}
