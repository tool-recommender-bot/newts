/*
 * Copyright 2014, The OpenNMS Group
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opennms.newts.api.search;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class BooleanQuery extends Query {

    private final List<BooleanClause> m_clauses;

    public BooleanQuery() {
        this(new ArrayList<BooleanClause>());
    }

    public BooleanQuery(List<BooleanClause> clauses) {
        m_clauses = clauses;
    }

    public void add(Query query, Operator op) {
        m_clauses.add(new BooleanClause(query, op));
    }

    public void add(BooleanClause clause) {
        m_clauses.add(clause);
    }

    public List<BooleanClause> getClauses() {
        return m_clauses;
    }

    @Override
    public BooleanQuery rewrite() {
        if (!needsRewrite()) {
            return this;
        }

        List<BooleanClause> booleanClauses = Lists.newArrayList();
        for (BooleanClause clause : m_clauses) {
            Query query = clause.getQuery();

            if (query instanceof BooleanQuery || query instanceof TermQuery) {
                booleanClauses.add(clause);
            } else {
                booleanClauses.add(new BooleanClause(query.rewrite(), clause.getOperator()));
            }
        }

        return new BooleanQuery(booleanClauses);
    }

    private boolean needsRewrite() {
        for (BooleanClause clause : m_clauses) {
            Query query = clause.getQuery();
            if (query instanceof BooleanQuery || query instanceof TermQuery) {
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
       if (obj == null) {
          return false;
       }
       if (getClass() != obj.getClass()) {
          return false;
       }
       final BooleanQuery other = (BooleanQuery) obj;

       return com.google.common.base.Objects.equal(m_clauses, other.m_clauses);
    }

    @Override
    public int hashCode() {
       return com.google.common.base.Objects.hashCode(
               m_clauses);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (BooleanClause clause : m_clauses) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
                sb.append(clause.getOperator());
                sb.append(" ");
            }

            sb.append("(");
            sb.append(clause.toString());
            sb.append(")");
        }
        return sb.toString();
    }
}
