/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;


/**
 * Instances of this class represent a qooxdoo theme of a certain type. Used to
 * assemble the Javascript code for qooxdoo themes.
 */
public class QxTheme {

  private final String id;

  private final String title;

  private final int type;

  private final String base;

  private final StringBuffer code;

  private boolean headWritten;

  private boolean tailWritten;

  private boolean valueWritten;

  /** Type for qooxdoo meta themes */
  public static final int META = 1;

  /** Type for qooxdoo font themes */
  public static final int FONT = 2;

  /** Type for qooxdoo color themes */
  public static final int COLOR = 3;

  /** Type for qooxdoo border themes */
  public static final int BORDER = 4;

  /** Type for qooxdoo icon themes */
  public static final int ICON = 5;

  /** Type for qooxdoo widget themes */
  public static final int WIDGET = 6;

  /** Type for qooxdoo apearance themes */
  public static final int APPEARANCE = 7;

  /**
   * Creates a new qooxdoo theme with the given, id, name, and type.
   *
   * @param id the fully qualified qx class name for the theme
   * @param title the name of the theme
   * @param type the type of the theme
   */
  public QxTheme( final String id, final String title, final int type ) {
    this( id, title, type, null );
  }

  /**
   * Creates a new qooxdoo theme with the given, id, name, type, and base class.
   *
   * @param id the fully qualified qx class name for the theme
   * @param title the name of the theme
   * @param type the type of the theme
   * @param base the fully qualified name of the qx theme to extend
   */
  public QxTheme( final String id,
                  final String title,
                  final int type,
                  final String base )
  {
    this.id = id;
    this.title = title;
    this.type = checkType( type );
    this.base = base;
    this.code = new StringBuffer();
    headWritten = false;
    tailWritten = false;
    valueWritten = false;
  }

  /**
   * Appends a number of key-value pairs to the generated theme. The given
   * Javascript code is appended as is, without any checks being performed.
   *
   * @param values Javascript code that adds the additional values
   */
  public void appendValues( final String values ) {
    beforeWriteValue();
    code.append( values );
    afterWriteValue();
  }

  /**
   * Appends the single uri entry to the generated widget or icon theme. Only
   * applicable for instances with type WIDGET or ICON.
   *
   * @param pathPrefix the prefix to map "widget/" or "icon/" to
   */
  public void appendUri( final String pathPrefix ) {
    beforeWriteValue();
    code.append( "    uri : \"" );
    code.append( pathPrefix );
    code.append( "\"" );
    afterWriteValue();
  }

  /**
   * Appends a key-value pair to the generated theme. Only applicable for
   * META theme writers.
   *
   * @param key the key to append
   * @param theme the value for the key
   */
  public void appendTheme( final String key, final String theme ) {
    beforeWriteValue();
    code.append( "    \"" + key + "\" : " );
    code.append( theme );
    afterWriteValue();
  }

  /**
   * Returns the Javascript code that represents this theme. Once this method
   * has been called, no values can be appended anymore.
   *
   * @return the generated theme code.
   */
  public String getJsCode() {
    if( !headWritten ) {
      writeHead();
    }
    if( !tailWritten ) {
      writeTail();
    }
    return code.toString();
  }

  private void beforeWriteValue() {
    if( !headWritten ) {
      writeHead();
    }
    if( tailWritten ) {
      throw new IllegalStateException( "Tail already written" );
    }
    if( valueWritten ) {
      code.append( ",\n" );
    }
  }

  private void afterWriteValue() {
    valueWritten = true;
  }

  private void writeHead() {
    code.append( "/* RAP theme file generated by QxTheme. */\n" );
    code.append( "qx.Theme.define( \"" + id + getNameSuffix() + "\",\n" );
    code.append( "{\n" );
    code.append( "  title : \"" + title + "\",\n" );
    if( base != null ) {
      code.append( "  extend : " + base + ",\n" );
    }
    code.append( "  " + getThemeKey() + " : {\n" );
    headWritten = true;
  }

  private void writeTail() {
    code.append( "\n" );
    code.append( "  }\n" );
    code.append( "} );\n" );
    tailWritten = true;
  }

  private int checkType( final int type ) {
    if( type != META
        && type != FONT
        && type != COLOR
        && type != BORDER
        && type != ICON
        && type != WIDGET
        && type != APPEARANCE )
    {
      throw new IllegalArgumentException( "illegal type" );
    }
    return type;
  }

  private String getNameSuffix() {
    String result = "";
    if( type == FONT ) {
      result = "Fonts";
    } else if( type == COLOR ) {
      result = "Colors";
    } else if( type == BORDER ) {
      result = "Borders";
    } else if( type == ICON ) {
      result = "Icons";
    } else if( type == WIDGET ) {
      result = "Widgets";
    } else if( type == APPEARANCE ) {
      result = "Appearances";
    }
    return result;
  }

  private String getThemeKey() {
    String result = null;
    if( type == META ) {
      result = "meta";
    } else if( type == FONT ) {
      result = "fonts";
    } else if( type == COLOR ) {
      result = "colors";
    } else if( type == BORDER ) {
      result = "borders";
    } else if( type == ICON ) {
      result = "icons";
    } else if( type == WIDGET ) {
      result = "widgets";
    } else if( type == APPEARANCE ) {
      result = "appearances";
    }
    return result;
  }
}
