package graphql.schema;


import graphql.DirectivesUtil;
import graphql.Internal;
import graphql.PublicApi;
import graphql.language.EnumValueDefinition;
import graphql.util.TraversalControl;
import graphql.util.TraverserContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static graphql.Assert.assertNotNull;
import static graphql.Assert.assertValidName;

/**
 * A graphql enumeration type has a limited set of values and this defines one of those unique values
 * <p>
 * See http://graphql.org/learn/schema/#enumeration-types for more details
 *
 * @see graphql.schema.GraphQLEnumType
 */
@PublicApi
public class GraphQLEnumValueDefinition implements GraphQLNamedSchemaElement, GraphQLDirectiveContainer {

    private final String name;
    private final String description;
    private final Object value;
    private final String deprecationReason;
    private final DirectivesUtil.DirectivesHolder directives;
    private final EnumValueDefinition definition;

    public static final String CHILD_DIRECTIVES = "directives";
    public static final String CHILD_APPLIED_DIRECTIVES = "appliedDirectives";

    @Internal
    private GraphQLEnumValueDefinition(String name,
                                       String description,
                                       Object value,
                                       String deprecationReason,
                                       List<GraphQLDirective> directives,
                                       List<GraphQLAppliedDirective> appliedDirectives,
                                       EnumValueDefinition definition) {
        assertValidName(name);
        assertNotNull(directives, () -> "directives cannot be null");

        this.name = name;
        this.description = description;
        this.value = value;
        this.deprecationReason = deprecationReason;
        this.directives = new DirectivesUtil.DirectivesHolder(directives, appliedDirectives);
        this.definition = definition;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Object getValue() {
        return value;
    }

    public boolean isDeprecated() {
        return deprecationReason != null;
    }

    public String getDeprecationReason() {
        return deprecationReason;
    }

    @Override
    public List<GraphQLDirective> getDirectives() {
        return directives.getDirectives();
    }

    @Override
    public Map<String, GraphQLDirective> getDirectivesByName() {
        return directives.getDirectivesByName();
    }

    @Override
    public Map<String, List<GraphQLDirective>> getAllDirectivesByName() {
        return directives.getAllDirectivesByName();
    }

    @Override
    public GraphQLDirective getDirective(String directiveName) {
        return directives.getDirective(directiveName);
    }

    public EnumValueDefinition getDefinition() {
        return definition;
    }

    @Override
    public List<GraphQLAppliedDirective> getAppliedDirectives() {
        return directives.getAppliedDirectives();
    }

    @Override
    public Map<String, List<GraphQLAppliedDirective>> getAllAppliedDirectivesByName() {
        return directives.getAllAppliedDirectivesByName();
    }

    @Override
    public GraphQLAppliedDirective getAppliedDirective(String directiveName) {
        return directives.getAppliedDirective(directiveName);
    }

    /**
     * This helps you transform the current GraphQLEnumValueDefinition into another one by starting a builder with all
     * the current values and allows you to transform it how you want.
     *
     * @param builderConsumer the consumer code that will be given a builder to transform
     *
     * @return a new field based on calling build on that builder
     */
    public GraphQLEnumValueDefinition transform(Consumer<Builder> builderConsumer) {
        Builder builder = newEnumValueDefinition(this);
        builderConsumer.accept(builder);
        return builder.build();
    }

    @Override
    public GraphQLSchemaElement copy() {
        return newEnumValueDefinition(this).build();
    }


    @Override
    public TraversalControl accept(TraverserContext<GraphQLSchemaElement> context, GraphQLTypeVisitor visitor) {
        return visitor.visitGraphQLEnumValueDefinition(this, context);
    }

    @Override
    public List<GraphQLSchemaElement> getChildren() {
        List<GraphQLSchemaElement> children = new ArrayList<>();
        children.addAll(directives.getDirectives());
        children.addAll(directives.getAppliedDirectives());
        return children;
    }

    @Override
    public SchemaElementChildrenContainer getChildrenWithTypeReferences() {
        return SchemaElementChildrenContainer.newSchemaElementChildrenContainer()
                .children(CHILD_DIRECTIVES, directives.getDirectives())
                .children(CHILD_APPLIED_DIRECTIVES, directives.getAppliedDirectives())
                .build();
    }

    @Override
    public GraphQLEnumValueDefinition withNewChildren(SchemaElementChildrenContainer newChildren) {
        return transform(builder ->
                builder.replaceDirectives(newChildren.getChildren(CHILD_DIRECTIVES))
                        .replaceAppliedDirectives(newChildren.getChildren(CHILD_APPLIED_DIRECTIVES))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "GraphQLEnumValueDefinition{" +
                "name='" + name + '\'' +
                '}';
    }

    public static Builder newEnumValueDefinition() {
        return new Builder();
    }

    public static Builder newEnumValueDefinition(GraphQLEnumValueDefinition existing) {
        return new Builder(existing);
    }

    @PublicApi
    public static class Builder extends GraphqlDirectivesContainerTypeBuilder<Builder,Builder> {
        private Object value;
        private String deprecationReason;
        private EnumValueDefinition definition;
        private final List<GraphQLDirective> directives = new ArrayList<>();

        public Builder() {
        }

        public Builder(GraphQLEnumValueDefinition existing) {
            this.name = existing.getName();
            this.description = existing.getDescription();
            this.value = existing.getValue();
            this.deprecationReason = existing.getDeprecationReason();
            copyExistingDirectives(existing);
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder deprecationReason(String deprecationReason) {
            this.deprecationReason = deprecationReason;
            return this;
        }

        public Builder definition(EnumValueDefinition definition) {
            this.definition = definition;
            return this;
        }

        public GraphQLEnumValueDefinition build() {
            return new GraphQLEnumValueDefinition(name,
                    description,
                    value,
                    deprecationReason,
                    sort(directives, GraphQLScalarType.class, GraphQLDirective.class),
                    sort(appliedDirectives, GraphQLScalarType.class, GraphQLAppliedDirective.class),
                    definition);
        }
    }
}
