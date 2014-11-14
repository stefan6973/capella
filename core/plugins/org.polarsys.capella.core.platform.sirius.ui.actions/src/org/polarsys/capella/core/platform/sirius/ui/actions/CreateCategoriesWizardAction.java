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
package org.polarsys.capella.core.platform.sirius.ui.actions;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.polarsys.capella.common.ef.command.AbstractReadWriteCommand;
import org.polarsys.capella.common.ui.actions.AbstractTigAction;

public class CreateCategoriesWizardAction extends AbstractTigAction {
  private CreateCategoriesController _createCatController;

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(IAction action_p) {
    // retrieve all the selected elements
    //
    final List<EObject> selection = WizardActionHelper.converToEObjectList(getSelectedElements());
    if (selection.isEmpty()) {
      return;
    }
    // if not valid selection return warning message
    if (UpdateCategoriesController.isValidSelection(selection)) {

      // create a Category with links common ancestor as container
      // fall down: set category container to root package

      AbstractReadWriteCommand doModelUpdateCmd = new AbstractReadWriteCommand() {
        /**
         * @see org.polarsys.capella.common.ef.command.AbstractCommand#getName()
         */
        @Override
        public String getName() {
          return "Create Category"; //$NON-NLS-1$
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
          handleChanges(selection);
        }

      };
      getExecutionManager().execute(doModelUpdateCmd);

    } else {
      WizardActionHelper.createMessageBox(getActiveShell(), Messages.UpdateCategoriesWizardAction_Warning_Message, SWT.ICON_INFORMATION);
    }

  }

  /**
   * @param selection
   */
  void handleChanges(final List<EObject> selection) {
    _createCatController = CreateCategoriesController.createCreateCategoriesController(selection);
    _createCatController.createAndAttachCategory(selection);
  }
}
