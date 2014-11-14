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
package org.polarsys.capella.common.helpers.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.polarsys.capella.common.helpers.export.utils.CSVWriter;

/**
 *
 */
public class CSVExporter extends AbstractExporter {
  
  protected char _delimiter;
  
  public CSVExporter(char delimiter) {
    _delimiter = delimiter;
  }

  /**
   * @see org.polarsys.capella.common.helpers.export.AbstractExporter#export(java.io.OutputStream, java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void export(OutputStream stream_p, Object data_p) throws IOException, ClassCastException {
   
    List<String[]> data = (List<String[]>) data_p;
    
    export(stream_p, data);
    
    return;
  }
  
  /**
   * @see #export(OutputStream, Object)
   */
  public void export(OutputStream stream_p, List<String[]> data_p) throws IOException  {
        
    CSVWriter writer= new CSVWriter(stream_p, _delimiter);
    
    for (String[] line: data_p) {
      if (null == line || line.length == 0) {
        writer.endRecord();
      }
      writer.writeRecord(line);
    }

    writer.flush();
    
    return;
  }

  /**
   * @see org.polarsys.capella.core.ui.metric.export.AbstractExporter#getExtension()
   */
  @Override
  public String getExtension() {
    return IFileExtensionConstants.CSV_EXTENSION;
  }

  /**
   * @see org.polarsys.capella.core.ui.metric.export.AbstractExporter#getDescription()
   */
  @Override
  public String getDescription() {
    return ExportMessages.csvDesc;
  }
  
}
