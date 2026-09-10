package com.liquido.core.common.validator;

import javax.validation.groups.Default;

/**
 * Common Valid Group: CRUD
 */
public interface CommonValidGroup {

    interface List extends Default {
    }

    interface Create extends Default {
    }

    interface Modify extends Default {
    }

    interface Delete extends Default {
    }

}
