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
package org.polarsys.capella.core.data.fa.ui.quickfix.resolver;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.polarsys.capella.common.ef.command.AbstractReadWriteCommand;
import org.polarsys.capella.common.helpers.TransactionHelper;
import org.polarsys.capella.core.data.fa.ComponentExchange;
import org.polarsys.capella.core.model.helpers.ComponentExchangeExt;
import org.polarsys.capella.core.validation.ui.ide.quickfix.AbstractCapellaMarkerResolution;

/**
 * Move Component exchanges to their default container
 */
public class DWF_DC_18_Resolver extends AbstractCapellaMarkerResolution {

  /**
   * {@inheritDoc}
   */
  public void run(IMarker marker_p) {
    final List<EObject> modelElements = getModelElements(marker_p);

    if (!modelElements.isEmpty()) {
      AbstractReadWriteCommand abstrctCommand = new AbstractReadWriteCommand() {

        @Override
        public String getName() {
          return getLabel();
        }

        public void run() {
          for (EObject object : modelElements) {
            if (object instanceof ComponentExchange) {
              ComponentExchangeExt.attachToDefaultContainer((ComponentExchange) object);
            }
          }
        }
      };

      // execute the command
      TransactionHelper.getExecutionManager(modelElements).execute(abstrctCommand);
      try {
        marker_p.delete();
      } catch (CoreException exception_p) {
        //Do nothing
      }
    }
  }
}
