package com.cognodyne.dw.cdi;

import java.util.Optional;

public interface CdiConfigurable {
    Optional<CdiConfiguration> getCdiConfiguration();
}
