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
package org.polarsys.capella.core.data.common.properties.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.polarsys.capella.core.data.capellacommon.CapellacommonPackage;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.common.properties.fields.ChangeEventKindGroup;
import org.polarsys.capella.core.ui.properties.fields.AbstractSemanticField;

public class ChangeEventSection extends StateEventSection {

  private ChangeEventKindGroup _kindGroup;

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControls(Composite parent_p, TabbedPropertySheetPage aTabbedPropertySheetPage_p) {
    boolean displayedInWizard = isDisplayedInWizard();
    super.createControls(parent_p, aTabbedPropertySheetPage_p);

    _kindGroup = new ChangeEventKindGroup(_rootParentComposite, getWidgetFactory(), true);
    _kindGroup.setDisplayedInWizard(displayedInWizard);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<AbstractSemanticField> getSemanticFields() {
    List<AbstractSemanticField> fields = new ArrayList<AbstractSemanticField>();
    fields.addAll(super.getSemanticFields());

    fields.add(_kindGroup);
    return fields;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void loadData(CapellaElement capellaElement_p) {
    super.loadData(capellaElement_p);
    _kindGroup.loadData(capellaElement_p, CapellacommonPackage.Literals.CHANGE_EVENT__KIND);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean select(Object toTest_p) {
    EObject eObjectToTest = super.selection(toTest_p);
    return ((eObjectToTest != null) && (eObjectToTest.eClass() == CapellacommonPackage.Literals.CHANGE_EVENT));
  }

}
