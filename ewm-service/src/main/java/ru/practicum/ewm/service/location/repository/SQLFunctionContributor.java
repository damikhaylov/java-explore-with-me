package ru.practicum.ewm.service.location.repository;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.FloatType;

public class SQLFunctionContributor implements MetadataBuilderContributor {
    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder.applySqlFunction("distance_from_location",
                new SQLFunctionTemplate(FloatType.INSTANCE, "distance(?1, ?2, ?3, ?4)"));
    }

}
