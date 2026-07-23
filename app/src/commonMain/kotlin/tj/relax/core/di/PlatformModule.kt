package tj.relax.core.di

import org.koin.core.module.Module

/** Provides bindings that need a platform-specific construction path (Room's database builder, the key-value stores it's paired with). */
expect fun platformModule(): Module
