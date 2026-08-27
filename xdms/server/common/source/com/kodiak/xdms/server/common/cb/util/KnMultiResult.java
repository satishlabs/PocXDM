/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.cb.util;

import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.query.QueryResult;

import java.util.List;
import java.util.Map;

public class KnMultiResult {
    private List<GetResult> getResults;
    private List<QueryResult> queryResults;
    private List<MutationResult> mutationResults;
    private Map<String, Throwable> errorResults;


    public List<GetResult> getGetResults() {
        return getResults;
    }

    public List<QueryResult> getQueryResults() {
        return queryResults;
    }

    public List<MutationResult> getMutationResults() {
        return mutationResults;
    }

    public Map<String, Throwable> getErrorResults() {
        return errorResults;
    }

    public static class Builder{
        private List<GetResult> getResults;
        private List<QueryResult> queryResults;
        private List<MutationResult> mutationResults;
        private Map<String, Throwable> errorResults;

        public Builder withGetResults(List<GetResult> results) {
            this.getResults = results;
            return this;
        }

        public Builder withQueryResults(List<QueryResult> results) {
            this.queryResults = results;
            return this;
        }

        public Builder withMutationResults(List<MutationResult> results) {
            this.mutationResults = results;
            return this;
        }

        public Builder withErrorResults(Map<String, Throwable> results) {
            this.errorResults = results;
            return this;
        }

        public KnMultiResult build(){
            KnMultiResult multiRes= new KnMultiResult();
            multiRes.getResults=this.getResults;
            multiRes.queryResults = this.queryResults;
            multiRes.mutationResults = this.mutationResults;
            multiRes.errorResults = this.errorResults;
            return multiRes;
        }
    }

    @Override
    public String toString() {
        return "MultiResult{" +
                "getResults=" + getResults +
                ", queryResults=" + queryResults +
                ", mutationResults=" + mutationResults +
                ", errorResults=" + errorResults +
                '}';
    }


}
