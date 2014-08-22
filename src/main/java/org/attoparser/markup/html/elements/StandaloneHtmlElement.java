/*
 * =============================================================================
 * 
 *   Copyright (c) 2012, The ATTOPARSER team (http://www.attoparser.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.attoparser.markup.html.elements;

import org.attoparser.AttoParseException;
import org.attoparser.IAttoHandleResult;
import org.attoparser.markup.html.IDetailedHtmlElementHandling;







/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public class StandaloneHtmlElement extends BasicHtmlElement {

    
    
    
    public StandaloneHtmlElement(final String name) {
        super(name);
    }

    
    
    
    
    @Override
    public IAttoHandleResult handleOpenElementStart(
            final char[] buffer, 
            final int nameOffset, final int nameLen, 
            final int line, final int col, 
            final HtmlElementStack stack, final IDetailedHtmlElementHandling handler) 
            throws AttoParseException {
        
        stack.openElement(this);
        
        return handler.handleHtmlStandaloneElementStart(this, false, buffer, nameOffset, nameLen, line, col);

    }

    
    @Override
    public IAttoHandleResult handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col,
            final HtmlElementStack stack, final IDetailedHtmlElementHandling handler) 
            throws AttoParseException {

        return handler.handleHtmlStandaloneElementEnd(this, false, buffer, nameOffset, nameLen, line, col);
        
    }


    
    
    @Override
    public IAttoHandleResult handleCloseElementStart(
            final char[] buffer, 
            final int nameOffset, final int nameLen, 
            final int line, final int col, 
            final HtmlElementStack stack, final IDetailedHtmlElementHandling handler) 
            throws AttoParseException {

        return handler.handleHtmlCloseElementStart(this, buffer, nameOffset, nameLen, line, col);

    }

    
    @Override
    public IAttoHandleResult handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col,
            final HtmlElementStack stack, final IDetailedHtmlElementHandling handler) 
            throws AttoParseException {

        final IAttoHandleResult result =
            handler.handleHtmlCloseElementEnd(this, buffer, nameOffset, nameLen, line, col);

        stack.closeElement();

        return result;
        
    }

    
    
    
}