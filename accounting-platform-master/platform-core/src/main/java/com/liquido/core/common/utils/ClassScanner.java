package com.liquido.core.common.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * Spring's class scanner
 */
@Slf4j
public class ClassScanner implements ResourceLoaderAware {

    /**
     * Save annotations to be excluded by filter rules
     */
    private List<TypeFilter> includeFilters;

    private List<TypeFilter> excludeFilters;

    private ResourcePatternResolver resourcePatternResolver =
            new PathMatchingResourcePatternResolver();

    private MetadataReaderFactory metadataReaderFactory =
            new CachingMetadataReaderFactory(this.resourcePatternResolver);

    public ClassScanner() {
    }

    public ClassScanner(final List<TypeFilter> includeFilters,
                        final List<TypeFilter> excludeFilters) {
        this.includeFilters = includeFilters;
        this.excludeFilters = excludeFilters;
    }

    public static void main(final String[] args) {
        final TypeFilter filter =
                (metadataReader, metadataReaderFactory) -> metadataReader.getClassMetadata()
                        .isAnnotation();
        final List<TypeFilter> includeFilter = Lists.newArrayList(filter);
        final Set<Class<?>> sets = new ClassScanner(includeFilter, null)
                .doScan("com.liquido.core.common.validator");
        for (final Class<?> c : sets) {
            System.out.println(c.getName());
        }
    }

    public final ResourceLoader getResourceLoader() {
        return this.resourcePatternResolver;
    }

    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourcePatternResolver =
                ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    public void addIncludeFilter(final TypeFilter includeFilter) {
        this.includeFilters.add(includeFilter);
    }

    public void addExcludeFilter(final TypeFilter excludeFilter) {
        this.excludeFilters.add(0, excludeFilter);
    }

    public void resetFilters(boolean useDefaultFilters) {
        this.includeFilters.clear();
        this.excludeFilters.clear();
    }

    public Set<Class<?>> doScan(final String basePackage) {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        try {
            final String packageSearchPath =
                    ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils
                            .convertClassNameToResourcePath(
                                    SystemPropertyUtils.resolvePlaceholders(basePackage)) +
                            "/**/*.class";

            final Resource[] resources =
                    this.resourcePatternResolver.getResources(packageSearchPath);

            if (resources == null || resources.length <= 0) {
                return classes;
            }

            for (final Resource resource : resources) {
                if (resource.isReadable()) {
                    final MetadataReader metadataReader =
                            this.metadataReaderFactory.getMetadataReader(resource);
                    if (matches(metadataReader)) {
                        try {
                            classes.add(Class.forName(
                                    metadataReader.getClassMetadata().getClassName()));
                        } catch (ClassNotFoundException e) {
                            log.error("ClassScanner.doScan ClassNotFoundException error:", e);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }

        return classes;
    }

    protected boolean matches(final MetadataReader metadataReader) throws IOException {
        if (Objects.nonNull(this.excludeFilters)) {
            for (final TypeFilter tf : this.excludeFilters) {
                if (tf.match(metadataReader, this.metadataReaderFactory)) {
                    return false;
                }
            }
        }

        if (Objects.nonNull(this.includeFilters)) {
            for (final TypeFilter tf : this.includeFilters) {
                if (tf.match(metadataReader, this.metadataReaderFactory)) {
                    return true;
                }
            }
        }

        return false;
    }
}
