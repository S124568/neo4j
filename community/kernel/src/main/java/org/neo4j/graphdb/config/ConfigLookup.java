package org.neo4j.graphdb.config;

import java.util.List;
import java.util.regex.Pattern;

import org.neo4j.helpers.Function;
import org.neo4j.helpers.Pair;

/**
 * A mechanism for {@link Setting} to find configuration values by key.
 *
 * This interface is available only for use, not for implementing. Implementing this interface is not expected, and
 * backwards compatibility is not guaranteed for implementors.
 */
public interface ConfigLookup extends Function<String,String>
{
    /**
     * Find all values where the key matches the given regular expression.
     * @param regex regular expression to match keys against
     * @return a list of all the key/value pairs that matched
     */
    List<Pair<String,String>> find( Pattern regex );
}
