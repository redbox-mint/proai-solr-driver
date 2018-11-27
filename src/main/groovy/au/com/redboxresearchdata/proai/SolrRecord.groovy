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

import proai.*
import proai.error.*
import java .io.*
/**
 * SolrMetadataFormat
 *
 * @author <a target='_' href='https://github.com/shilob'>Shilo Banihit</a>
 *
 */
class SolrRecord implements Record {
  static String field_metadataSchema = 'metadataSchema_s'
  static String field_xml = 'xml_s'

  String itemID
  String prefix
  String sourceInfo
  String xml

  public SolrRecord(Map json) {
    itemID = json['id']
    prefix = json[field_metadataSchema]
    xml = json[field_xml]
  }
}
