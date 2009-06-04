/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ToolItemLCA_Test extends TestCase {

  public void testCheckPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.CHECK );
    RWTFixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    RWTFixture.preserveWidgets();
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( Props.SELECTION_INDICES ) );
    RWTFixture.clearPreserved();
    item.setSelection( true );
    RWTFixture.preserveWidgets();
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( Props.SELECTION_INDICES ) );
    testPreserveValues( display, item );
    display.dispose();
  }

  public void testDropDownPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.DROP_DOWN );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, item );
    display.dispose();
  }

  public void testPushPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.PUSH );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, item );
    display.dispose();
  }

  public void testRadioPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.RADIO );
    RWTFixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    RWTFixture.preserveWidgets();
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( Props.SELECTION_INDICES ) );
    RWTFixture.clearPreserved();
    item.setSelection( true );
    RWTFixture.preserveWidgets();
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( Props.SELECTION_INDICES ) );
    testPreserveValues( display, item );
    display.dispose();
  }

  public void testSeparatorPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.SEPARATOR );
    Button button = new Button( tb, SWT.PUSH );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    assertEquals( null, adapter.getPreserved( Props.CONTROL ) );
    item.setControl( button );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item );
    assertEquals( button, adapter.getPreserved( Props.CONTROL ) );
    RWTFixture.clearPreserved();
    testPreserveValues( display, item );
    display.dispose();
  }

  public void testCheckItemSelected() {
    final boolean[] wasEventFired = { false };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    final ToolItem item = new ToolItem( toolBar, SWT.CHECK );
    item.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        wasEventFired[ 0 ] = true;
        assertEquals( null, event.item );
        assertSame( item, event.getSource() );
        assertEquals( true, event.doit );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, item.getSelection() );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String toolItemId = WidgetUtil.getId( item );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( toolItemId + ".selection", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, toolItemId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( true, wasEventFired[ 0 ] );
  }
  
  public void testRadioItemSelected() {
    Display display = new Display();
    Shell shell = new Shell( display );
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );
    ToolItem item0 = new ToolItem( toolBar, SWT.RADIO );
    item0.setSelection( true );
    ToolItem item1 = new ToolItem( toolBar, SWT.RADIO );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String item1Id = WidgetUtil.getId( item1 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( item1Id + ".selection", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, item1Id );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertFalse( item0.getSelection() );
    assertTrue( item1.getSelection() );
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    final ToolItem item = new ToolItem( tb, SWT.CHECK );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( item );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    ToolItemLCA itemLCA = new ToolItemLCA();
    item.setText( "benny" );
    itemLCA.renderChanges( item );
    String expected = "setLabel( \"benny\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    item.setSelection( true );
    itemLCA.renderChanges( item );
    assertTrue( Fixture.getAllMarkup().endsWith( "setChecked( true );" ) );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    itemLCA.renderChanges( item );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testReadData() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( toolBar, SWT.CHECK );
    String itemId = WidgetUtil.getId( item );
    // read changed selection
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, itemId );
    Fixture.fakeRequestParam( itemId + ".selection", "true" );
    WidgetUtil.getLCA( item ).readData( item );
    assertEquals( Boolean.TRUE, Boolean.valueOf( item.getSelection() ) );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, itemId );
    Fixture.fakeRequestParam( itemId + ".selection", "false" );
    WidgetUtil.getLCA( item ).readData( item );
    assertEquals( Boolean.FALSE, Boolean.valueOf( item.getSelection() ) );
  }

  public void testGetImage() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( toolBar, SWT.CHECK );

    Image enabledImage = Graphics.getImage( RWTFixture.IMAGE1 );
    Image disabledImage = Graphics.getImage( RWTFixture.IMAGE2 );
    assertNull( ToolItemLCAUtil.getImage( item ) );

    item.setImage( enabledImage );
    assertSame( enabledImage, ToolItemLCAUtil.getImage( item ) );

    item.setImage( enabledImage );
    item.setDisabledImage( disabledImage );
    assertSame( enabledImage, ToolItemLCAUtil.getImage( item ) );

    item.setEnabled( false );
    assertSame( disabledImage, ToolItemLCAUtil.getImage( item ) );

    item.setDisabledImage( null );
    assertSame( enabledImage, ToolItemLCAUtil.getImage( item ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  private void testPreserveValues( final Display display, final ToolItem item )
  {
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    assertEquals( "", adapter.getPreserved( Props.TOOLTIP ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    item.addSelectionListener( selectionListener );
    item.setText( "some text" );
    item.setEnabled( false );
    item.setToolTipText( "tooltip text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    if( ( item.getStyle() & SWT.SEPARATOR ) == 0 ) {
      assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    }
    assertEquals( Boolean.TRUE, hasListeners );
    assertEquals( "tooltip text", adapter.getPreserved( Props.TOOLTIP ) );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
  }
}
