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

import org.apache.solr.common.SolrDocumentList
import org.apache.solr.common.params.*
import org.apache.solr.client.solrj.*
import org.apache.solr.client.solrj.impl.*
import org.apache.solr.client.solrj.util.ClientUtils
import groovy.json.*

/**
 * Solr Data source
 *
 * @author <a target='_' href='https://github.com/shilob'>Shilo Banihit</a>
 *
 */
class SolrData {
  String url
  String recordTypeField
  String typeIdentity
  String typeMetadataFormat
  String typeSet
  String typeRecord
  String idMeta

  def clients = [:]

  public HttpSolrClient getClient(String core) {
    def clientId = "${url}_${core}"
    final String solrUrl = "${url}/solr/${core}";
    clients[clientId] = new HttpSolrClient.Builder(solrUrl)
      .withConnectionTimeout(10000)
      .withSocketTimeout(60000)
      .build();
    return clients[clientId]
  }

  public Date getLatestModDate(core) {
    final params = ['q': "id:${idMeta}"]
    final resp = getClient(core).query(new MapSolrParams(params))
    final docs = resp.getResults()
    return docs[0].latest_dt
  }

  public String getIdentity(core) {
    final params = ['q': "${recordTypeField}:${typeIdentity}"]
    final resp = getClient(core).query(new MapSolrParams(params))
    final docs = resp.getResults()
    return docs[0].xml_s
  }

  public List<SolrMetadataFormat> getMetadataFormats(core) {
    final formats = []
    final params = ['q': "${recordTypeField}:${typeMetadataFormat}"]
    final resp = getClient(core).query(new MapSolrParams(params))
    final docs = resp.getResults()
    docs.each { jsonDoc ->
      formats << new SolrMetadataFormat(jsonDoc)
    }
    return formats
  }

  public List<SolrSetInfo> getSets(core) {
    final sets = []
    final params = ['q': "${recordTypeField}:${typeSet}"]
    final resp = getClient(core).query(new MapSolrParams(params))
    final docs = resp.getResults()
    docs.each { jsonDoc ->
      sets << new SolrSetInfo(jsonDoc)
    }
    return sets
  }

  public List<SolrRecord> getRecords(core) {
    final records = []
    final params = ['q': "${recordTypeField}:${typeRecord}"]
    final resp = getClient(core).query(new MapSolrParams(params))
    final docs = resp.getResults()
    docs.each { jsonDoc ->
      def solrRec = new SolrRecord(jsonDoc)
      solrRec.sourceInfo = core
      records << solrRec
    }
    return records
  }

  public SolrDocumentList getRecordsPage(core, int start, int rows) {

    final params = ['q': "${recordTypeField}:${typeRecord}", 'start': start, 'rows': rows]
    final resp = getClient(core).query(new MapSolrParams(params))

    return resp.getResults()
  }

  public SolrRecord getRecord(core, id, format) {
    final params = ['q': "${recordTypeField}:${typeRecord} AND id:${ClientUtils.escapeQueryChars(id)} AND metadataSchema_s:${format}"]
    final resp = getClient(core).query(new MapSolrParams(params))
    final docs = resp.getResults()
    if (docs.getNumFound() > 0) {
      return new SolrRecord(docs[0])
    }
    return null;
  }
}
