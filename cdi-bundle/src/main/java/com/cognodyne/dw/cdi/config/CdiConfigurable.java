package com.cognodyne.dw.cdi.config;

import java.util.Optional;

public interface CdiConfigurable {
    Optional<CdiConfiguration> getCdiConfiguration();
}
