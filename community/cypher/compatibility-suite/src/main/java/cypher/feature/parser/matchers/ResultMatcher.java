/*
 * Copyright (c) 2002-2016 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cypher.feature.parser.matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Result;

import static cypher.feature.parser.matchers.UnorderedListMatcher.findMatch;

public class ResultMatcher implements Matcher<Result>
{
    private final List<RowMatcher> rowMatchers;

    public ResultMatcher( List<RowMatcher> rowMatchers )
    {
        this.rowMatchers = rowMatchers;
    }

    @Override
    public boolean matches( Result value )
    {
        boolean result = matchesWithoutNecessarilyExhausting( value );
        exhaust( value );
        return result;
    }

    private boolean matchesWithoutNecessarilyExhausting( Result value )
    {
        List<RowMatcher> mutableCopy = new ArrayList<>( rowMatchers );
        while ( value.hasNext() && !mutableCopy.isEmpty() )
        {
            Map<String,Object> nextRow = value.next();
            int index = findMatch( mutableCopy, nextRow );
            if ( index < 0 )
            {
                return false;
            }
            mutableCopy.remove( index );
        }

        boolean nothingLeftInReal = !value.hasNext();
        boolean nothingLeftInMatcher = mutableCopy.isEmpty();
        return nothingLeftInMatcher && nothingLeftInReal;
    }

    public boolean matchesOrdered( Result value )
    {
        boolean matches = matchesOrderedWithoutNecessarilyExhausting( value );
        exhaust( value );
        return matches;
    }

    private boolean matchesOrderedWithoutNecessarilyExhausting( Result value )
    {
        boolean matches = true;
        int counter = 0;
        while ( value.hasNext() && counter < rowMatchers.size() )
        {
            matches &= rowMatchers.get( counter++ ).matches( value.next() );
        }

        boolean nothingLeftInReal = !value.hasNext();
        boolean nothingLeftInMatcher = counter == rowMatchers.size();
        return matches && nothingLeftInMatcher && nothingLeftInReal;
    }

    private void exhaust( Result value )
    {
        // exhaust the result to get a full toString()
        while ( value.hasNext() )
        {
            value.next();
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder( "Expected result of:\n" );
        int i = 1;
        for ( RowMatcher row : rowMatchers )
        {
            sb.append( "[" ).append( i++ ).append( "] " ).append( row ).append( "\n" );
        }
        return sb.toString();
    }
}
