/*******************************************************************************
 * Copyright (c) 2006, 2015 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *    Thales - initial API and implementation
 *******************************************************************************/
package org.polarsys.capella.test.recrpl.ju;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.polarsys.capella.common.flexibility.properties.schema.IPropertyContext;
import org.polarsys.capella.common.re.handlers.replicable.ReplicableElementHandlerHelper;
import org.polarsys.capella.core.transition.common.constants.ITransitionConstants;
import org.polarsys.capella.core.transition.common.constants.ITransitionSteps;
import org.polarsys.capella.core.transition.common.handlers.activity.IActivityExtender;
import org.polarsys.capella.core.transition.common.handlers.options.DefaultOptionsHandler;
import org.polarsys.capella.core.transition.common.handlers.options.OptionsHandlerHelper;
import org.polarsys.kitalpha.cadence.core.api.parameter.ActivityParameters;
import org.polarsys.kitalpha.transposer.rules.handler.rules.api.IContext;

/**
 * An activiry extender to run a test before the end of the transition (and so on before disposing all handlers)
 */
public class RecRplCommandManager implements IActivityExtender {

  /**
   * {@inheritDoc}
   */
  @Override
  public IStatus init(IContext context) {
    return Status.OK_STATUS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IStatus dispose(IContext context) {
    return Status.OK_STATUS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IStatus preActivity(IContext context, String activityIdentifier, ActivityParameters activityParams) {
    if ("DifferencesFilteringActivity".equals(activityIdentifier)) {
    	//throw new UnsupportedOperationException();
//      if (_currentTest != null) {
//        _currentTest.filteringTest(context);
//      }
    }
    return Status.OK_STATUS;
  } 
  
  protected static Map<String, Object> properties = new HashMap<String, Object>();
//  protected static String propertyName = null;
//  protected static Object value = null;
  protected static IContext context;
  
  public static void push(String propertyName, Object value) {
    properties.put(propertyName, value);
  }
  
  public static IContext getContext() {
  	return context;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public IStatus postActivity(IContext contextParameter, String activityIdentifier, ActivityParameters activityParams) {  	
  	if (ITransitionSteps.INITIALIZE_TRANSITION.equals(activityIdentifier)) {
      IPropertyContext propertyContext =
          ((DefaultOptionsHandler) OptionsHandlerHelper.getInstance(contextParameter)).getPropertyContext(contextParameter,
              (String) contextParameter.get(ITransitionConstants.OPTIONS_SCOPE));
      for (Entry<String, Object> entry : properties.entrySet()) {
        propertyContext.setCurrentValue(propertyContext.getProperties().getProperty(entry.getKey()), entry.getValue());
      }
    } 
  	if ("FinalizeTransitionActivity".equals(activityIdentifier)) {
  		context = contextParameter;
  		System.out.println(ReplicableElementHandlerHelper.getInstance(getContext()));  		
  	}
    return Status.OK_STATUS;
  }

	public static void clear() {
	  properties.clear();
	}
}
