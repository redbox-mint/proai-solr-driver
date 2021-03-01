/*******************************************************************************
 * Copyright (C) 2018 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
package au.com.redboxresearchdata.proai

import proai.*;
import proai.driver.*;
import proai.error.*;
import proai.driver.impl.*
/**
 * Solr driver
 *
 * @author <a target='_' href='https://github.com/shilob'>Shilo Banihit</a>
 *
 */
class SolrDriver implements OAIDriver {
  static final String SOLR_BASEURL = "proai.driver.solr.baseUrl"
  static final String SOLR_CORE = "proai.driver.solr.core"
  static final String SOLR_TYPE_FIELD = "proai.driver.solr.recordTypeField"
  static final String SOLR_TYPE_METADATAFORMAT = "proai.driver.solr.recordTypeMetadataFormat"
  static final String SOLR_TYPE_IDENTITY = "proai.driver.solr.recordTypeIdentity"
  static final String SOLR_TYPE_SET = "proai.driver.solr.recordTypeSet"
  static final String SOLR_TYPE_RECORD = "proai.driver.solr.recordTypeRecord"
  static final String SOLR_FIELD_SPEC_SET = "proai.driver.solr.fieldSpecSet"
  static final String SOLR_FIELD_SPEC_NAME = "proai.driver.solr.fieldSpecName"
  static final String SOLR_FIELD_RECORD_SCHEMA = "proai.driver.solr.fieldRecordSchema"
  static final String SOLR_FIELD_RECORD_XML = "proai.driver.solr.fieldRecordXml"
  static final String SOLR_FIELD_METADATA_PREFIX = "proai.driver.solr.fieldMetadataPrefix"
  static final String SOLR_FIELD_METADATA_NAMESPACE = "proai.driver.solr.fieldMetadataNamespace"
  static final String SOLR_FIELD_METADATA_SCHEMA = "proai.driver.solr.fieldMetadataSchema"
  static final String SOLR_ID_OAIPMH_META = "proai.driver.solr.idOaipmhMeta"

  SolrData solrData;
  String core;

  /**
    * Initialize from properties.
    *
    * @param props the implementation-specific initialization properties.
    * @throws RepositoryException if required properties are missing/bad,
    *         or initialization failed for any reason.
    */
   public void init(Properties props) throws RepositoryException {
     solrData = new SolrData(
       url: props.getProperty(SOLR_BASEURL),
       recordTypeField: props.getProperty(SOLR_TYPE_FIELD),
       typeIdentity: props.getProperty(SOLR_TYPE_IDENTITY),
       typeMetadataFormat: props.getProperty(SOLR_TYPE_METADATAFORMAT),
       typeSet: props.getProperty(SOLR_TYPE_SET),
       typeRecord: props.getProperty(SOLR_TYPE_RECORD),
       idMeta: props.getProperty(SOLR_ID_OAIPMH_META)
     )
     core = props.getProperty(SOLR_CORE)
     SolrSetInfo.field_spec = props.getProperty(SOLR_FIELD_SPEC_SET)
     SolrSetInfo.field_name = props.getProperty(SOLR_FIELD_SPEC_NAME)
     SolrRecord.field_metadataSchema = props.getProperty(SOLR_FIELD_RECORD_SCHEMA)
     SolrRecord.field_xml = props.getProperty(SOLR_FIELD_RECORD_XML)
     SolrMetadataFormat.field_prefix = props.getProperty(SOLR_FIELD_METADATA_PREFIX)
     SolrMetadataFormat.field_namespace = props.getProperty(SOLR_FIELD_METADATA_NAMESPACE)
     SolrMetadataFormat.field_schema = props.getProperty(SOLR_FIELD_METADATA_SCHEMA)
   }

   /**
    * Write information about the repository to the given PrintWriter.
    *
    * <p>
    *   This will be a well-formed XML chunk beginning with an
    *   <code>Identify</code> element, as described in
    *   <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#Identify">section
    *   4.2 of the OAI-PMH 2.0 specification</a>.
    * <p>
    *
    * @throws RepositoryException if there is a problem reading from the repository.
    */
   public void write(PrintWriter out) throws RepositoryException {
     out.write(solrData.getIdentity(core))
   }

   /**
    * Get the latest date that something changed in the remote repository.
    *
    * <p>
    *   If this is greater than the previously-aquired latestDate,
    *   the formats, setInfos, and identity will be retrieved again,
    *   and it will be used as the "until" date for the next record query.
    * </p>
    */
   public Date getLatestDate() throws RepositoryException {
     def dt = solrData.getLatestModDate(core)
     return dt
   }

   /**
    * Get an iterator over a list of MetadataFormat objects representing
    * all OAI metadata formats currently supported by the repository.
    *
    * @see proai.MetadataFormat
    */
   public RemoteIterator<? extends MetadataFormat> listMetadataFormats() throws RepositoryException {
     def formats = solrData.getMetadataFormats(core)
     return new RemoteIteratorImpl<MetadataFormat>(formats.iterator())
   }

   /**
    * Get an iterator over a list of SetInfo objects representing all
    * OAI sets currently supported by the repository.
    *
    * <p>
    *   The content will be a well-formed XML chunk beginning with a
    *   <code>set</code> element, as described in
    *   <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListSets">section
    *   4.6 of the OAI-PMH 2.0 specification</a>.
    * <p>
    *
    * @see proai.SetInfo
    */
   public RemoteIterator<? extends SetInfo> listSetInfo() throws RepositoryException {
     def sets = solrData.getSets(core)
     return new RemoteIteratorImpl<SetInfo>(sets.iterator())
   }

   /**
    * Get an iterator of <code>Record</code> objects representing all records
    * in the format indicated by mdPrefix, which have changed in the given date
    * range.
    *
    * <p><strong>Regarding dates:</strong>
    * <em>If from is not null, the date is greater than (non-inclusive)
    * Until must be specified, and it is less than or equal to (inclusive).</em>
    *
    * @see proai.Record
    */
   public RemoteIterator<? extends Record> listRecords(Date from,
                                     Date until,
                                     String mdPrefix) throws RepositoryException
   {
     return new RemoteIteratorImpl<Record>(new SolrRecordIterator(core,solrData));
   }

   /**
    * Write the XML of the record whose source info is given.
    *
    * SourceInfo MUST NOT contain newlines. Otherwise, the format is up to the
    * implementation.
    *
    * The Record implementation produces these strings, and the OAIDriver
    * implementation should know how to use them to produce the XML.
    *
    * The record must be a well-formed XML chunk beginning with a
    * <code>record</code> element, as described in
    * <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#GetRecord">section
    * 4.1 of the OAI-PMH 2.0 specification</a>.
    */
   public void writeRecordXML(String itemID,
                              String mdPrefix,
                              String sourceInfo,
                              PrintWriter writer) throws RepositoryException
   {
     def record = solrData.getRecord(core, itemID, mdPrefix)
     if (record) {
       writer.write(record.xml)
     }
   }

   /**
    * Release any resources held by the driver.
    */
   public void close() throws RepositoryException {

   }
}
