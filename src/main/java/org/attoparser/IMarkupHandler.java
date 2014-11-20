/*
 * =============================================================================
 * 
 *   Copyright (c) 2012-2014, The ATTOPARSER team (http://www.attoparser.org)
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
package org.attoparser;

import org.attoparser.config.ParseConfiguration;
import org.attoparser.select.ParseSelection;

/**
 * <p>
 *   Interface to be implemented by all Markup Handlers.
 * </p>
 * <p>
 *   Markup handlers are the objects that receive the events produced during parsing and perform the operations
 *   the users need. <strong>This interface is the main entry point to use AttoParser</strong>.
 * </p>
 * <p>
 *   Markup handlers can be <strong>stateful</strong>, which means that a new instance of the markup handler
 *   class should be created for each parsing operation. In such case, it is not required that these implementations
 *   are <em>thread-safe</em>.
 * </p>
 * <p>
 *   There is an abstract, basic, no-op implementation of this interface called
 *   {@link org.attoparser.AbstractMarkupHandler} which can be used for easily creating new handlers by overriding
 *   only the relevant event handling methods.
 * </p>
 * <p>
 *   Note also there is a <strong>simplified</strong> version of this interface that reduces the number of events
 *   and also simplifies the operations on textual buffers, called {@link org.attoparser.simple.ISimpleMarkupHandler},
 *   which can be easily used with the convenience ad-hoc parser class
 *   {@link org.attoparser.simple.SimpleMarkupParser}.
 * </p>
 * <p>
 *   AttoParser provides several useful implementations of this interface out-of-the-box:
 * </p>
 *
 * <h3>Markup output</h3>
 * <dl>
 *
 *   <dt>{@link org.attoparser.output.OutputMarkupHandler}</dt><dd>
 *     For writing the received events to a
 *     specified {@link java.io.Writer} object, without any loss of information (case, whitespaces, etc.). This
 *     handler is useful for performing filtering/transformation operations on the parsed markup, placing this
 *     handler at the end of the handler chain so that it outputs the final results of such operation.
 *   </dd>
 *
 *   <dt>{@link org.attoparser.output.TextOutputMarkupHandler}</dt>
 *   <dd>
 *     For writing the received events to a
 *     specified {@link java.io.Writer} object as mere text, ignoring all non-text events. This will effectively
 *     strip all markup elements, comments, DOCTYPEs, etc. from the original markup.
 *   </dd>
 * </dl>
 *
 * <h3>Format conversion and transformation operations</h3>
 * <dl>
 *
 *   <dt>{@link org.attoparser.dom.DOMBuilderMarkupHandler}</dt>
 *   <dd>
 *     For building a DOM tree as a result of parsing
 *     a document. This DOM tree will be created using the classes at the <tt>org.attoparser.dom</tt> package.
 *     This handler can be more easily applied by using the convenience ad-hoc parser
 *     class {@link org.attoparser.dom.DOMMarkupParser}.
 *   </dd>
 *
 *   <dt>{@link org.attoparser.simple.SimplifierMarkupHandler}</dt>
 *   <dd>
 *     For transforming the produced markup
 *     parsing events into a much simpler format, removing much of the complexity of these parsing events
 *     and allowing users to create their handlers by means of the
 *     {@link org.attoparser.simple.ISimpleMarkupHandler} interface. Note this handler can be more easily
 *     applied by using the convenience ad-hoc parser class {@link org.attoparser.simple.SimpleMarkupParser}.
 *   </dd>
 *
 *   <dt>{@link org.attoparser.minimize.MinimizeHtmlMarkupHandler}</dt>
 *   <dd>
 *       For minimizing (compacting) HTML markup: remove excess white space, unquote attributes, etc.
 *   </dd>
 *
 * </dl>
 *
 * <h3>Fragment selection and event management</h3>
 * <dl>
 *
 *   <dt>{@link org.attoparser.select.BlockSelectorMarkupHandler}</dt>
 *   <dd>
 *     For applying <em>block selection</em> (element + subtree) on the parsed markup, based on a set
 *     of specified <em>markup selectors</em> (see {@link org.attoparser.select}).
 *   </dd>
 *
 *   <dt>{@link org.attoparser.select.NodeSelectorMarkupHandler}</dt>
 *   <dd>
 *     For applying <em>node selection</em> (element, no subtree) on the parsed markup, based on a set
 *     of specified <em>markup selectors</em> (see {@link org.attoparser.select}).
 *   </dd>
 *
 *   <dt>{@link org.attoparser.select.AttributeSelectionMarkingMarkupHandler}</dt>
 *   <dd>
 *     For synthetically adding an attribute (with the specified name) to markup elements displaying which of the
 *     specified selectors (block or node) match those markup elements.
 *   </dd>
 *
 *   <dt>{@link org.attoparser.duplicate.DuplicateMarkupHandler}</dt>
 *   <dd>
 *     For duplicating parsing events, sending each
 *     of them to two different implementations if {@link org.attoparser.IMarkupHandler}.
 *   </dd>
 *
 * </dl>
 *
 * <h3>Testing and Debugging</h3>
 * <dl>
 *
 *   <dt>{@link org.attoparser.prettyhtml.PrettyHtmlMarkupHandler}</dt>
 *   <dd>
 *     For creating an HTML
 *     document visually explaining all the events happened during the parsing of a document:
 *     elements, attributes, auto-closing of elements, unmatched artifacts, etc.
 *   </dd>
 *
 *   <dt>{@link org.attoparser.trace.TraceBuilderMarkupHandler}</dt>
 *   <dd>
 *     For building a trace of parsing events (a
 *     list of {@link org.attoparser.trace.MarkupTraceEvent} objects) detailing all the events launched
 *     during the parsing of a document.
 *   </dd>
 *
 * </dl>
 *
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.0.0
 *
 */
public interface IMarkupHandler {


    /**
     * <p>
     *   Sets the {@link org.attoparser.config.ParseConfiguration} object that will be used during the parsing
     *   operation. This object will normally have been specified to the parser object during its instantiation
     *   or initialization.
     * </p>
     * <p>
     *   This method is always called by the parser <strong>before</strong> calling any other event handling method.
     * </p>
     * <p>
     *   Note that this method can be <strong>safely ignored by most implementations</strong>, as there are
     *   very few scenarios in which this kind of interaction would be consisdered relevant.
     * </p>
     *
     * @param parseConfiguration the configuration object.
     */
    public void setParseConfiguration(final ParseConfiguration parseConfiguration);


    /**
     * <p>
     *   Sets the {@link org.attoparser.ParseStatus} object that will be used during the parsing operation. This
     *   object can be used for instructing the parser about specific low-level conditions arisen during event
     *   handling.
     * </p>
     * <p>
     *   This method is always called by the parser <strong>before</strong> calling any other event handling method.
     * </p>
     * <p>
     *   Note that this method can be <strong>safely ignored by most implementations</strong>, as there are
     *   very few and very specific scenarios in which this kind of interaction with the parser would be needed.
     *   It is therefore mainly for internal use.
     * </p>
     *
     * @param status the status object.
     */
    public void setParseStatus(final ParseStatus status);


    /**
     * <p>
     *   Sets the {@link org.attoparser.select.ParseSelection} object that represents the different levels of
     *   selectors (if any) that are currently active for the fired events.
     * </p>
     * <p>
     *   This method is always called by the parser <strong>before</strong> calling any other event handling method.
     * </p>
     * <p>
     *   Note that this method can be <strong>safely ignored by most implementations</strong>, as there are
     *   very few scenarios in which this kind of interaction would be consisdered relevant.
     * </p>
     *
     * @param selection the selection object.
     */
    public void setParseSelection(final ParseSelection selection);


    /**
     * <p>
     *   Sets the {@link org.attoparser.IMarkupParser} object that will be actually parsing the document and calling
     *   this handler's methods.
     * </p>
     * <p>
     *   This method is always called by the parser <strong>before</strong> calling any other event handling method.
     * </p>
     * <p>
     *   Note that this method can be <strong>safely ignored by most implementations</strong>, as there are
     *   very few and very specific scenarios in which this kind of interaction with the parser would be needed.
     *   It is therefore mainly (though not only) for internal use.
     * </p>
     *
     * @param parser the parser object.
     */
    public void setParser(final IMarkupParser parser);


    /**
     * <p>
     *   Sets the {@link org.attoparser.IMarkupHandler} object that represents the first element of the handler
     *   chain, i.e. the original handler that is passed to the parser's <tt>parse*</tt> methods. This
     *   object can be used for calling the parser again (obtained with {@link #setParser(IMarkupParser)}) from one
     *   of its handlers in order to perform nested parsing operations.
     * </p>
     * <p>
     *   This method is always called by the parser <strong>before</strong> calling any other event handling method.
     * </p>
     * <p>
     *   Note that this method can be <strong>safely ignored by most implementations</strong>, as there are
     *   very few and very specific scenarios in which this kind of interaction with the parser would be needed.
     *   It is therefore mainly (though not only) for internal use.
     * </p>
     *
     * @param handlerChain the status object.
     */
    public void setHandlerChain(final IMarkupHandler handlerChain);




    /**
     * <p>
     *   Called at the beginning of document parsing.
     * </p>
     *
     * @param startTimeNanos the current time (in nanoseconds) obtained when parsing starts.
     * @param line the line of the document where parsing starts (usually number 1).
     * @param col the column of the document where parsing starts (usually number 1).
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleDocumentStart(final long startTimeNanos, final int line, final int col)
            throws ParseException;



    /**
     * <p>
     *   Called at the end of document parsing.
     * </p>
     *
     * @param endTimeNanos the current time (in nanoseconds) obtained when parsing ends.
     * @param totalTimeNanos the difference between current times at the start and end of
     *        parsing (in nanoseconds).
     * @param line the line of the document where parsing ends (usually the last one).
     * @param col the column of the document where the parsing ends (usually the last one).
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws ParseException;



    /**
     * <p>
     *   Called when a XML Declaration is found.
     * </p>
     * <p>
     *   Five [offset, len] pairs are provided for five partitions (<i>outer</i>,
     *   <i>keyword</i>, <i>version</i>, <i>encoding</i> and <i>standalone</i>):
     * </p>
     * <p>
     *   <tt>&lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;</tt><br>
     *   <tt><b>|&nbsp;[K]&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[V]&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[ENC]&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[S]&nbsp;&nbsp;|</b></tt><br>
     *   <tt><b>[OUTER------------------------------------------------]</b></tt>
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param keywordOffset offset for the <i>keyword</i> partition.
     * @param keywordLen length of the <i>keyword</i> partition.
     * @param keywordLine the line in the original document where the <i>keyword</i> partition starts.
     * @param keywordCol the column in the original document where the <i>keyword</i> partition starts.
     * @param versionOffset offset for the <i>version</i> partition.
     * @param versionLen length of the <i>version</i> partition.
     * @param versionLine the line in the original document where the <i>version</i> partition starts.
     * @param versionCol the column in the original document where the <i>version</i> partition starts.
     * @param encodingOffset offset for the <i>encoding</i> partition.
     * @param encodingLen length of the <i>encoding</i> partition.
     * @param encodingLine the line in the original document where the <i>encoding</i> partition starts.
     * @param encodingCol the column in the original document where the <i>encoding</i> partition starts.
     * @param standaloneOffset offset for the <i>standalone</i> partition.
     * @param standaloneLen length of the <i>standalone</i> partition.
     * @param standaloneLine the line in the original document where the <i>standalone</i> partition starts.
     * @param standaloneCol the column in the original document where the <i>standalone</i> partition starts.
     * @param outerOffset offset for the <i>outer</i> partition.
     * @param outerLen length of the <i>outer</i> partition.
     * @param line the line in the original document where this artifact starts.
     * @param col the column in the original document where this artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleXmlDeclaration(
            final char[] buffer,
            final int keywordOffset, final int keywordLen,
            final int keywordLine, final int keywordCol,
            final int versionOffset, final int versionLen,
            final int versionLine, final int versionCol,
            final int encodingOffset, final int encodingLen,
            final int encodingLine, final int encodingCol,
            final int standaloneOffset, final int standaloneLen,
            final int standaloneLine, final int standaloneCol,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException;



    /**
     * <p>
     *   Called when a DOCTYPE clause is found.
     * </p>
     * <p>
     *   This method reports the DOCTYPE clause splitting it into its different
     *   parts.
     * </p>
     * <p>
     *   Seven [offset, len] pairs are provided for seven partitions (<i>outer</i>,
     *   <i>keyword</i>, <i>elementName</i>, <i>type</i>, <i>publicId</i>,
     *   <i>systemId</i> and <i>internalSubset</i>) of the DOCTYPE clause:
     * </p>
     * <p>
     *   <tt>&lt;!DOCTYPE html PUBLIC ".........." ".........." [................]&gt;</tt><br>
     *   <tt><b>|&nbsp;[KEYWO]&nbsp;[EN]&nbsp;[TYPE]&nbsp;&nbsp;[PUBLICID]&nbsp;&nbsp;&nbsp;[SYSTEMID]&nbsp;&nbsp;&nbsp;[INTERNALSUBSET]&nbsp;|</b></tt><br>
     *   <tt><b>[OUTER------------------------------------------------------------]</b></tt>
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param keywordOffset offset for the <i>keyword</i> partition.
     * @param keywordLen length of the <i>keyword</i> partition.
     * @param keywordLine the line in the original document where the <i>keyword</i> partition starts.
     * @param keywordCol the column in the original document where the <i>keyword</i> partition starts.
     * @param elementNameOffset offset for the <i>elementName</i> partition.
     * @param elementNameLen length of the <i>elementName</i> partition.
     * @param elementNameLine the line in the original document where the <i>elementName</i> partition starts.
     * @param elementNameCol the column in the original document where the <i>elementName</i> partition starts.
     * @param typeOffset offset for the <i>type</i> partition.
     * @param typeLen length of the <i>type</i> partition.
     * @param typeLine the line in the original document where the <i>type</i> partition starts.
     * @param typeCol the column in the original document where the <i>type</i> partition starts.
     * @param publicIdOffset offset for the <i>publicId</i> partition.
     * @param publicIdLen length of the <i>publicId</i> partition.
     * @param publicIdLine the line in the original document where the <i>publicId</i> partition starts.
     * @param publicIdCol the column in the original document where the <i>publicId</i> partition starts.
     * @param systemIdOffset offset for the <i>systemId</i> partition.
     * @param systemIdLen length of the <i>systemId</i> partition.
     * @param systemIdLine the line in the original document where the <i>systemId</i> partition starts.
     * @param systemIdCol the column in the original document where the <i>systemId</i> partition starts.
     * @param internalSubsetOffset offset for the <i>internalSubsetId</i> partition.
     * @param internalSubsetLen length of the <i>internalSubsetId</i> partition.
     * @param internalSubsetLine the line in the original document where the <i>internalSubsetId</i> partition starts.
     * @param internalSubsetCol the column in the original document where the <i>internalSubsetId</i> partition starts.
     * @param outerOffset offset for the <i>outer</i> partition.
     * @param outerLen length of the <i>outer</i> partition.
     * @param outerLine the line in the original document where this artifact starts.
     * @param outerCol the column in the original document where this artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleDocType(
            final char[] buffer,
            final int keywordOffset, final int keywordLen,
            final int keywordLine, final int keywordCol,
            final int elementNameOffset, final int elementNameLen,
            final int elementNameLine, final int elementNameCol,
            final int typeOffset, final int typeLen,
            final int typeLine, final int typeCol,
            final int publicIdOffset, final int publicIdLen,
            final int publicIdLine, final int publicIdCol,
            final int systemIdOffset, final int systemIdLen,
            final int systemIdLine, final int systemIdCol,
            final int internalSubsetOffset, final int internalSubsetLen,
            final int internalSubsetLine, final int internalSubsetCol,
            final int outerOffset, final int outerLen,
            final int outerLine, final int outerCol)
            throws ParseException;



    /**
     * <p>
     *   Called when a CDATA section is found.
     * </p>
     * <p>
     *   Two [offset, len] pairs are provided for two partitions (<i>outer</i> and <i>content</i>):
     * </p>
     * <p>
     *   <tt>&lt;![CDATA[ this is a CDATA section ]]&gt;</tt><br>
     *   <tt><b>|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[CONTENT----------------]&nbsp;&nbsp;|</b></tt><br>
     *   <tt><b>[OUTER------------------------------]</b></tt>
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param contentOffset offset for the <i>content</i> partition.
     * @param contentLen length of the <i>content</i> partition.
     * @param outerOffset offset for the <i>outer</i> partition.
     * @param outerLen length of the <i>outer</i> partition.
     * @param line the line in the original document where this artifact starts.
     * @param col the column in the original document where this artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleCDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException;



    /**
     * <p>
     *   Called when a comment is found.
     * </p>
     * <p>
     *   Two [offset, len] pairs are provided for two partitions (<i>outer</i> and <i>content</i>):
     * </p>
     * <p>
     *   <tt>&lt;!-- this is a comment --&gt;</tt><br>
     *   <tt><b>|&nbsp;&nbsp;&nbsp;[CONTENT----------]&nbsp;&nbsp;|</b></tt><br>
     *   <tt><b>[OUTER-------------------]</b></tt>
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param contentOffset offset for the <i>content</i> partition.
     * @param contentLen length of the <i>content</i> partition.
     * @param outerOffset offset for the <i>outer</i> partition.
     * @param outerLen length of the <i>outer</i> partition.
     * @param line the line in the original document where this artifact starts.
     * @param col the column in the original document where this artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleComment(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException;


    /**
     * <p>
     *   Called when a <i>text</i> artifact is found.
     * </p>
     * <p>
     *   A sequence of chars is considered to be <i>text</i> when no structures of any kind are
     *   contained inside it. In markup parsers, for example, this means no tags (a.k.a. <i>elements</i>),
     *   DOCTYPE's, processing instructions, etc. are contained in the sequence.
     * </p>
     * <p>
     *   Text sequences might include any number of new line and/or control characters.
     * </p>
     * <p>
     *   Text artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported texts should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param offset the offset (position in buffer) where the text artifact starts.
     * @param len the length (in chars) of the text artifact, starting in offset.
     * @param line the line in the original document where this text artifact starts.
     * @param col the column in the original document where this text artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException;


    /**
     * <p>
     *   Called when a standalone element (an element with no closing tag) is found. The name of
     *   the element is also reported.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param minimized whether the element has been found minimized (&lt;element/&gt;)in code or not.
     * @param line the line in the original document where this artifact starts.
     * @param col the column in the original document where this artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws ParseException;

    /**
     * <p>
     *   Called when the end of a standalone element (an element with no closing tag) is found
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param minimized whether the element has been found minimized (&lt;element/&gt;)in code or not.
     * @param line the line in the original document where the element ending structure appears.
     * @param col the column in the original document where the element ending structure appears.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws ParseException;


    /**
     * <p>
     *   Called when an open element (an <i>open tag</i>) is found. The name of
     *   the element is also reported.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param line the line in the original document where this artifact starts.
     * @param col the column in the original document where this artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException;

    /**
     * <p>
     *   Called when the end of an open element (an <i>open tag</i>) is found.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param line the line in the original document where the element ending structure appears.
     * @param col the column in the original document where the element ending structure appears.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException;


    /**
     * <p>
     *   Called for signaling the start of an auto-open element (a synthetic <i>open tag</i>),
     *   created for adapting parsed markup to a specification such as, for example, HTML5. The name of the
     *   element is also reported.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param line the line in the original document where this artifact starts.
     * @param col the column in the original document where this artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleAutoOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException;

    /**
     * <p>
     *   Called for signaling the end of an auto-open element (a synthetic <i>open tag</i>),
     *   created for adapting parsed markup to a specification such as, for example, HTML5. The name of the
     *   element is also reported.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param line the line in the original document where the element ending structure appears.
     * @param col the column in the original document where the element ending structure appears.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleAutoOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException;


    /**
     * <p>
     *   Called when the start of a close element (a <i>close tag</i>) is found. The name of
     *   the element is also reported.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param line the line in the original document where this artifact starts.
     * @param col the column in the original document where this artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException;

    /**
     * <p>
     *   Called when the end of a close element (a <i>close tag</i>) is found.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param line the line in the original document where the element ending structure appears.
     * @param col the column in the original document where the element ending structure appears.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException;


    /**
     * <p>
     *   Called for signaling the start of an auto-close element (a synthetic <i>close tag</i>),
     *   created for balancing an unclosed tag. The name of the element is also reported.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param line the line in the original document where this artifact starts.
     * @param col the column in the original document where this artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleAutoCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException;

    /**
     * <p>
     *   Called for signaling the end of an auto-close element, created for
     *   balancing an unclosed tag.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param line the line in the original document where the element ending structure appears.
     * @param col the column in the original document where the element ending structure appears.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleAutoCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException;


    /**
     * <p>
     *   Called when the start of an unmatched close element (<i>close tag</i>) is found. The name of
     *   the element is also reported.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param line the line in the original document where this artifact starts.
     * @param col the column in the original document where this artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleUnmatchedCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException;

    /**
     * <p>
     *   Called when the end of an unmatched close element (<i>close tag</i>) is found.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset the offset (position in buffer) where the element name appears.
     * @param nameLen the length (in chars) of the element name.
     * @param line the line in the original document where the element ending structure appears.
     * @param col the column in the original document where the element ending structure appears.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleUnmatchedCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException;


    /**
     * <p>
     *   Called when an attribute is found.
     * </p>
     * <p>
     *   Three [offset, len] pairs are provided for three partitions (<i>name</i>,
     *   <i>operator</i>, <i>valueContent</i> and <i>valueOuter</i>):
     * </p>
     * <p>
     *   <tt>class="basic_column"</tt><br>
     *   <tt><b>[NAM]*&nbsp;[VALUECONTE]|   (*) = [OPERATOR]</b></tt><br>
     *   <tt><b>|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[VALUEOUTER--]</b></tt><br>
     *   <tt><b>[OUTER-------------]</b></tt>
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param nameOffset offset for the <i>name</i> partition.
     * @param nameLen length of the <i>name</i> partition.
     * @param nameLine the line in the original document where the <i>name</i> partition starts.
     * @param nameCol the column in the original document where the <i>name</i> partition starts.
     * @param operatorOffset offset for the <i>operator</i> partition.
     * @param operatorLen length of the <i>operator</i> partition.
     * @param operatorLine the line in the original document where the <i>operator</i> partition starts.
     * @param operatorCol the column in the original document where the <i>operator</i> partition starts.
     * @param valueContentOffset offset for the <i>valueContent</i> partition.
     * @param valueContentLen length of the <i>valueContent</i> partition.
     * @param valueOuterOffset offset for the <i>valueOuter</i> partition.
     * @param valueOuterLen length of the <i>valueOuter</i> partition.
     * @param valueLine the line in the original document where the <i>value</i> (outer) partition starts.
     * @param valueCol the column in the original document where the <i>value</i> (outer) partition starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol)
            throws ParseException;


    /**
     * <p>
     *   Called when an amount of white space is found inside an element.
     * </p>
     * <p>
     *   This attribute separators can contain any amount of whitespace, including
     *   line feeds:
     * </p>
     * <p>
     *   <tt>&lt;div id="main"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;class="basic_column"&gt;</tt><br>
     *   <tt><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[INNWSP]</b></tt><br>
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param offset offset for the artifact.
     * @param len length of the artifact.
     * @param line the line in the original document where the artifact starts.
     * @param col the column in the original document where the artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleInnerWhiteSpace(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException;



    /**
     * <p>
     *   Called when a Processing Instruction is found.
     * </p>
     * <p>
     *   Three [offset, len] pairs are provided for three partitions (<i>outer</i>,
     *   <i>target</i> and <i>content</i>):
     * </p>
     * <p>
     *   <tt>&lt;?xls-stylesheet somePar1="a" somePar2="b"?&gt;</tt><br>
     *   <tt><b>|&nbsp;[TARGET------]&nbsp;[CONTENT----------------]&nbsp;|</b></tt><br>
     *   <tt><b>[OUTER-------------------------------------]</b></tt>
     * </p>
     * <p>
     *   Note that, although XML Declarations have the same format as processing instructions,
     *   they are not considered as such and therefore are handled through a different handling
     *   method.
     * </p>
     * <p>
     *   Artifacts are reported using the document <tt>buffer</tt> directly, and this buffer
     *   should not be considered to be immutable, so reported structures should be copied if they need
     *   to be stored (either by copying <tt>len</tt> chars from the buffer <tt>char[]</tt> starting
     *   in <tt>offset</tt> or by creating a <tt>String</tt> from it using the same specification).
     * </p>
     * <p>
     *   <b>Implementations of this handler should never modify the document buffer.</b>
     * </p>
     *
     * @param buffer the document buffer (not copied)
     * @param targetOffset offset for the <i>target</i> partition.
     * @param targetLen length of the <i>target</i> partition.
     * @param targetLine the line in the original document where the <i>target</i> partition starts.
     * @param targetCol the column in the original document where the <i>target</i> partition starts.
     * @param contentOffset offset for the <i>content</i> partition.
     * @param contentLen length of the <i>content</i> partition.
     * @param contentLine the line in the original document where the <i>content</i> partition starts.
     * @param contentCol the column in the original document where the <i>content</i> partition starts.
     * @param outerOffset offset for the <i>outer</i> partition.
     * @param outerLen length of the <i>outer</i> partition.
     * @param line the line in the original document where this artifact starts.
     * @param col the column in the original document where this artifact starts.
     * @throws ParseException if any exceptions occur during handling.
     */
    public void handleProcessingInstruction(
            final char[] buffer,
            final int targetOffset, final int targetLen,
            final int targetLine, final int targetCol,
            final int contentOffset, final int contentLen,
            final int contentLine, final int contentCol,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException;

}