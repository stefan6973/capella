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
package org.polarsys.capella.core.tiger;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import org.polarsys.capella.core.tiger.impl.Transfo;
import org.polarsys.capella.core.tiger.impl.TransfoRule;
import org.polarsys.capella.core.tiger.impl.TransfoRuleBase;

/**
 *
 */
public interface ITransfo extends Map<String, Object> {

  public abstract String getUid();

  public abstract void setUid(String uid);

  /**
   * @param rule
   * @see org.polarsys.capella.core.tiger.impl.TransfoRuleBase#addRule(org.polarsys.capella.core.tiger.impl.TransfoRule)
   */
  public abstract void addRule(TransfoRule rule);

  /**
   * @param ruleClass
   * @see org.polarsys.capella.core.tiger.impl.TransfoRuleBase#loadRule(java.lang.Class)
   */
  public abstract void loadRule(Class<?> ruleClass);

  /**
   * @param rulePkgName
   * @param classNames
   * @throws ClassNotFoundException
   * @see org.polarsys.capella.core.tiger.impl.TransfoRuleBase#loadRules(java.lang.String, java.lang.String[])
   */
  public abstract void loadRules(String rulePkgName, String[] classNames) throws ClassNotFoundException;

  /**
   * @param rulePkgName
   * @throws ClassNotFoundException
   * @see org.polarsys.capella.core.tiger.impl.TransfoRuleBase#loadRules(java.lang.String)
   */
  public abstract void loadRules(String rulePkgName) throws ClassNotFoundException;

  /**
   * 
   * @param element
   * @param transfo
   * @return
   * @throws TransfoException 
   * @see TransfoRuleBase#findMatchingRule(EObject, Transfo)
   */
  public abstract ITransfoRule findMatchingRule(EObject element) throws TransfoException;

  /**
   * 
   * @param element
   * @return
   * @throws TransfoException 
   */
  public abstract ITransfoRule findCachedMatchingRule(EObject element) throws TransfoException;

  /**
   *
   */
  public abstract String toHtml();
  
  /**
   * @see java.util.AbstractMap#toString()
   */
  public abstract String toString();

  /**
   * @return
   */
  public abstract List<IFinalizer> getFinalizers();

  /**
   * @return defined resolvers
   */
  public abstract List<IResolver> getResolvers();

}
