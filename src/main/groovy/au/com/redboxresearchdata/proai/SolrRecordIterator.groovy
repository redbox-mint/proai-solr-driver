package au.com.redboxresearchdata.proai

import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrDocumentList
import proai.Record

class SolrRecordIterator implements Iterator<Record>{

    private SolrData solrData;
    private String core;
    private SolrDocumentList currentResult
    private int rows = 20
    private int currentIndex = 0;
    private int currentStart = 0;

    public SolrRecordIterator(String core, SolrData data) {
        this.core = core;
        this.solrData = data;
        this.currentResult = this.solrData.getRecordsPage(core,currentStart, this.rows)
    }

    @Override
    boolean hasNext() {
        if(this.currentIndex == this.currentResult.size()) {
            this.currentStart = this.currentStart + this.rows;
            this.currentIndex = 0;
            this.currentResult = this.solrData.getRecordsPage(core, this.currentStart, this.rows)
        }

        if(this.currentResult.size() == 0) {
            return false;
        }
        if(this.currentIndex < this.currentResult.size()) {
            return true;
        }
        if((this.currentResult.getNumFound() - 1) > (this.currentStart + this.currentIndex)) {
            return true;
        }
        return false;
    }

    @Override
    Record next() {
            SolrDocument doc = this.currentResult.get(this.currentIndex);
            SolrRecord record = new SolrRecord(doc);
            record.sourceInfo = core;
            this.currentIndex++;
            return record;
    }
}
