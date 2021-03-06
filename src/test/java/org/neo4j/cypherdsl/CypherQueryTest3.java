package org.neo4j.cypherdsl;

import org.junit.Test;
import org.neo4j.cypherdsl.querydsl.Attribute;

public class CypherQueryTest3 extends AbstractCypherTest {

    @Test
    public void unwindLiteralCollection() {
        assertQueryEquals(CYPHER + "UNWIND [1,2] as x RETURN x", new CypherQuery() {{
            unwinds(collection(literal(1), literal(2)), identifier("x")).returns(identifier("x"));
        }}.toString());
    }

    @Test
    public void unwindParameter() {
        assertQueryEquals(CYPHER + "UNWIND {numbers} as x RETURN x", new CypherQuery() {{
            unwinds(param("numbers"), identifier("x")).returns(identifier("x"));
        }}.toString());
    }

    @Test
    public void unwindWithCreate() {
        assertQueryEquals(CYPHER + "UNWIND {pairs} as pair MATCH (`pair.x`),(`pair.y`) CREATE (`pair.x`)-[:edge]->(`pair.y`)", new CypherQuery() {{
            unwinds(param("pairs"), identifier("pair"))
                    .match(node(identifier("pair.x")), node(identifier("pair.y")))
                    .create(node(identifier("pair.x")).out("edge").node(identifier("pair.y")));
        }}.toString());
    }

    @Test
    public void unwindMatch() {
        assertQueryEquals(CYPHER + "MATCH (n)-[r*1..2]-(m) UNWIND r as item RETURN DISTINCT item", new CypherQuery() {{
            matches(node(identifier("n")).both().hops(1, 2).as(identifier("r")).node(identifier("m")))
                    .unwind(identifier("r"), identifier("item"))
                    .returns(distinct(identifier("item")));
        }}.toString());
    }

    @Test
    public void unwindWhere() {
        assertQueryEquals(CYPHER + "MATCH (n)-[r*1..2]-(m) WHERE n.id=\"1\" UNWIND r as item RETURN DISTINCT item", new CypherQuery() {{
            matches(node(identifier("n")).both().hops(1, 2).as(identifier("r")).node(identifier("m")))
                    .where(identifier("n").property("id").eq(literal("1")))
                    .unwind(identifier("r"), identifier("item"))
                    .returns(distinct(identifier("item")));
        }}.toString());
    }

    @Test
    public void enumProperty() {
        assertQueryEquals(CYPHER + "MATCH (n) WHERE n.Id=\"1\" RETURN n", new CypherQuery() {{
            matches(node(identifier("n"))).where(identifier("n").property(Attribute.Id).eq("1"))
                    .returns(identifier("n"));
        }}.toString());
    }

    @Test
    public void enumLabel() {
        assertQueryEquals(CYPHER + "MATCH (n:Type) RETURN n", new CypherQuery() {{
            matches(node(identifier("n").label(Attribute.Type)))
                    .returns(identifier("n"));
        }}.toString());
    }

    @Test
    public void enumValueString() {
        assertQueryEquals(CYPHER + "MATCH (n {Type:\"Object\"}) RETURN n", new CypherQuery() {{
            matches(node(identifier("n")).values(value(Attribute.Type, "Object")))
                    .returns(identifier("n"));
        }}.toString());
    }

    @Test
    public void enumValueExpression() {
        assertQueryEquals(CYPHER + "MATCH (n {Type:n.Id}) RETURN n", new CypherQuery() {{
            matches(node(identifier("n")).values(value(Attribute.Type, identifier("n").property(Attribute.Id))))
                    .returns(identifier("n"));
        }}.toString());
    }
}
