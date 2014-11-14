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
import org.polarsys.capella.core.data.common.properties.Messages;
import org.polarsys.capella.core.data.common.properties.fields.TimeEventKindGroup;
import org.polarsys.capella.core.ui.properties.fields.AbstractSemanticField;
import org.polarsys.capella.core.ui.properties.fields.TextValueGroup;

public class TimeEventSection extends StateEventSection {

  private TimeEventKindGroup _kindGroup;
  private TextValueGroup _valueGroup;

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControls(Composite parent_p, TabbedPropertySheetPage aTabbedPropertySheetPage_p) {
    boolean displayedInWizard = isDisplayedInWizard();
    super.createControls(parent_p, aTabbedPropertySheetPage_p);

    _kindGroup = new TimeEventKindGroup(_rootParentComposite, getWidgetFactory(), true);
    _kindGroup.setDisplayedInWizard(displayedInWizard);

    _valueGroup = new TextValueGroup(_rootParentComposite, Messages.getString("TimeEvent.Time"), getWidgetFactory()); //$NON-NLS-1$
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<AbstractSemanticField> getSemanticFields() {

    List<AbstractSemanticField> fields = new ArrayList<AbstractSemanticField>();
    fields.addAll(super.getSemanticFields());

    fields.add(_kindGroup);
    fields.add(_valueGroup);

    return fields;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void loadData(CapellaElement capellaElement_p) {
    super.loadData(capellaElement_p);
    _kindGroup.loadData(capellaElement_p, CapellacommonPackage.Literals.TIME_EVENT__KIND);
    _valueGroup.loadData(capellaElement_p, CapellacommonPackage.Literals.TIME_EVENT__TIME);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean select(Object toTest_p) {
    EObject eObjectToTest = super.selection(toTest_p);
    return ((eObjectToTest != null) && (eObjectToTest.eClass() == CapellacommonPackage.eINSTANCE.getTimeEvent()));
  }
}
