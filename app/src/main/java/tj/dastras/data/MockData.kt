package tj.dastras.data

object MockData {

    val categories = listOf(
        Category(1,  "Мясо",            "🥩", 0xFFFFE5E5, 124),
        Category(2,  "Овощи",           "🥦", 0xFFE5F5E5, 87),
        Category(3,  "Фрукты",          "🍎", 0xFFFFF3E0, 63),
        Category(4,  "Молочные",        "🥛", 0xFFE5F0FF, 95),
        Category(5,  "Напитки",         "🥤", 0xFFEDE5FF, 78),
        Category(6,  "Химия",           "🧴", 0xFFFFE5F5, 56),
        Category(7,  "Детские",         "🍼", 0xFFFFF5E5, 43),
        Category(8,  "Хлеб",            "🍞", 0xFFFFF9E5, 38),
    )

    val banners = listOf(
        Banner(
            id = 1,
            title = "Скидки до 40%",
            subtitle = "На все свежие овощи и фрукты",
            imageUrl = "https://picsum.photos/seed/banner1/800/400",
            backgroundColor = 0xFF1B5E20,
            badgeText = "до 40%"
        ),
        Banner(
            id = 2,
            title = "Новая коллекция",
            subtitle = "Премиальные сыры из Европы",
            imageUrl = "https://picsum.photos/seed/banner2/800/400",
            backgroundColor = 0xFF0D47A1,
            badgeText = "Новинки"
        ),
        Banner(
            id = 3,
            title = "Кэшбэк 10%",
            subtitle = "С картой RELAX на весь алкоголь",
            imageUrl = "https://picsum.photos/seed/banner3/800/400",
            backgroundColor = 0xFF4A148C,
            badgeText = "10% кэшбэк"
        ),
        Banner(
            id = 4,
            title = "1+1=3",
            subtitle = "На все молочные продукты",
            imageUrl = "https://picsum.photos/seed/banner4/800/400",
            backgroundColor = 0xFF880E4F,
            badgeText = "1+1=3"
        ),
    )

    val products = listOf(
        Product(
            id = 1, categoryId = 1,
            name = "Говядина мраморная Prime",
            brand = "RELAX Exclusive",
            imageUrl = "https://picsum.photos/seed/beef1/400/400",
            price = 1290.0, oldPrice = 1590.0,
            unit = "кг", weight = "~500 г",
            rating = 4.8f, reviewCount = 234,
            description = "Мраморная говядина высшей категории. Идеально подходит для стейков.",
            composition = "Говядина охлаждённая 100%",
        ),
        Product(
            id = 2, categoryId = 1,
            name = "Куриное филе охлаждённое",
            brand = "Золотой петух",
            imageUrl = "https://picsum.photos/seed/chicken1/400/400",
            price = 349.0, oldPrice = 420.0,
            unit = "кг", weight = "1 кг",
            rating = 4.6f, reviewCount = 567,
            description = "Свежее куриное филе без кожи и костей.",
            composition = "Куриное мясо охлаждённое 100%",
        ),
        Product(
            id = 3, categoryId = 2,
            name = "Авокадо спелый Hass",
            brand = "BioFresh",
            imageUrl = "https://picsum.photos/seed/avocado/400/400",
            price = 189.0,
            unit = "шт", weight = "200-250 г",
            rating = 4.7f, reviewCount = 189,
            isNew = true,
            description = "Спелый авокадо сорта Hass, богатый полезными жирами.",
        ),
        Product(
            id = 4, categoryId = 3,
            name = "Клубника свежая Senga",
            brand = "BioFresh",
            imageUrl = "https://picsum.photos/seed/strawberry/400/400",
            price = 290.0, oldPrice = 350.0,
            unit = "упак", weight = "500 г",
            rating = 4.9f, reviewCount = 412,
            description = "Свежая клубника прямо с фермы. Сладкая и ароматная.",
        ),
        Product(
            id = 5, categoryId = 4,
            name = "Молоко Parmalat Ultra",
            brand = "Parmalat",
            imageUrl = "https://picsum.photos/seed/milk1/400/400",
            price = 129.0,
            unit = "шт", weight = "1 л",
            rating = 4.5f, reviewCount = 891,
            description = "Ультрапастеризованное молоко 3.2% жирности.",
            composition = "Молоко коровье нормализованное",
        ),
        Product(
            id = 6, categoryId = 4,
            name = "Сыр Бри President",
            brand = "President",
            imageUrl = "https://picsum.photos/seed/cheese1/400/400",
            price = 399.0, oldPrice = 479.0,
            unit = "упак", weight = "125 г",
            rating = 4.8f, reviewCount = 156,
            description = "Мягкий сыр с белой плесенью из Нормандии.",
        ),
        Product(
            id = 7, categoryId = 5,
            name = "Сок Rich Апельсин",
            brand = "Rich",
            imageUrl = "https://picsum.photos/seed/juice1/400/400",
            price = 199.0, oldPrice = 229.0,
            unit = "шт", weight = "1 л",
            rating = 4.4f, reviewCount = 324,
        ),
        Product(
            id = 8, categoryId = 5,
            name = "Вода Evian негазированная",
            brand = "Evian",
            imageUrl = "https://picsum.photos/seed/water1/400/400",
            price = 149.0,
            unit = "шт", weight = "1.5 л",
            rating = 4.7f, reviewCount = 1204,
        ),
        Product(
            id = 9, categoryId = 2,
            name = "Томаты черри Premium",
            brand = "BioFresh",
            imageUrl = "https://picsum.photos/seed/tomato1/400/400",
            price = 159.0, oldPrice = 199.0,
            unit = "упак", weight = "250 г",
            rating = 4.6f, reviewCount = 278,
            isNew = true,
        ),
        Product(
            id = 10, categoryId = 3,
            name = "Манго Alfonso спелый",
            brand = "Тропиканка",
            imageUrl = "https://picsum.photos/seed/mango1/400/400",
            price = 249.0,
            unit = "шт", weight = "300-400 г",
            rating = 4.9f, reviewCount = 89,
            isNew = true,
        ),
        Product(
            id = 11, categoryId = 4,
            name = "Йогурт Activia натуральный",
            brand = "Activia",
            imageUrl = "https://picsum.photos/seed/yogurt1/400/400",
            price = 89.0, oldPrice = 109.0,
            unit = "шт", weight = "290 г",
            rating = 4.3f, reviewCount = 567,
        ),
        Product(
            id = 12, categoryId = 1,
            name = "Лосось атлантический",
            brand = "Arctic Fresh",
            imageUrl = "https://picsum.photos/seed/salmon1/400/400",
            price = 890.0, oldPrice = 1090.0,
            unit = "кг", weight = "~400 г",
            rating = 4.8f, reviewCount = 134,
            description = "Охлаждённый лосось атлантический премиум-класса.",
        ),
    )

    val promotions = listOf(
        Promotion(
            id = 1,
            title = "Фестиваль сыров",
            subtitle = "Скидки до 30% на все сыры мира",
            discount = "−30%",
            imageUrl = "https://picsum.photos/seed/promo1/600/400",
            endDate = "До 15 июня",
            backgroundColor = 0xFF1A237E,
        ),
        Promotion(
            id = 2,
            title = "Зелёная пятница",
            subtitle = "Все овощи и фрукты по специальным ценам",
            discount = "−25%",
            imageUrl = "https://picsum.photos/seed/promo2/600/400",
            endDate = "До 20 июня",
            backgroundColor = 0xFF1B5E20,
        ),
        Promotion(
            id = 3,
            title = "Мясной weekend",
            subtitle = "Купи 2 кг — получи скидку 40%",
            discount = "−40%",
            imageUrl = "https://picsum.photos/seed/promo3/600/400",
            endDate = "Только в выходные",
            backgroundColor = 0xFFB71C1C,
        ),
    )

    val bonusTransactions = listOf(
        BonusTransaction(1,  "Покупка в магазине",         +350, true,  "Сегодня, 14:23",    "ORD-20240607-001"),
        BonusTransaction(2,  "Оплата бонусами",            -200, false, "Вчера, 10:15",       "ORD-20240606-003"),
        BonusTransaction(3,  "Покупка в магазине",         +120, true,  "3 июня, 18:40",      "ORD-20240603-002"),
        BonusTransaction(4,  "Бонус за отзыв",             +50,  true,  "1 июня, 09:00",      ""),
        BonusTransaction(5,  "Покупка в магазине",         +890, true,  "28 мая, 16:12",      "ORD-20240528-001"),
        BonusTransaction(6,  "День рождения — подарок!",  +500, true,  "20 мая, 00:00",      ""),
        BonusTransaction(7,  "Покупка в магазине",         +230, true,  "15 мая, 12:30",      "ORD-20240515-002"),
        BonusTransaction(8,  "Оплата бонусами",            -400, false, "10 мая, 11:20",      "ORD-20240510-001"),
    )

    val loyaltyLevels = listOf(
        LoyaltyLevel("Старт",    0,     4999,  1f,  0xFFC0C0C0, listOf("1% кэшбэк бонусами", "Базовые скидки")),
        LoyaltyLevel("Серебро",  5000,  19999, 3f,  0xFF9E9E9E, listOf("3% кэшбэк бонусами", "Скидки до 10%", "Приоритетная доставка")),
        LoyaltyLevel("Золото",   20000, 49999, 5f,  0xFFD4AF37, listOf("5% кэшбэк бонусами", "Скидки до 20%", "Бесплатная доставка", "Эксклюзивные акции")),
        LoyaltyLevel("Платина",  50000, 99999, 7f,  0xFF5C6BC0, listOf("7% кэшбэк бонусами", "Скидки до 30%", "Бесплатная доставка", "Персональный менеджер")),
        LoyaltyLevel("Элит",    100000, Int.MAX_VALUE, 10f, 0xFF0F172A, listOf("10% кэшбэк бонусами", "Скидки до 40%", "VIP доставка", "Персональный менеджер", "Эксклюзивные товары")),
    )

    val currentUser = UserProfile(
        name          = "Алексей Иванов",
        phone         = "+7 (900) 123-45-67",
        email         = "aleksey@example.com",
        avatarUrl     = "https://picsum.photos/seed/avatar1/200/200",
        cardNumber    = "RELAX 4821 7630 5512",
        bonusBalance  = 2840,
        totalSpent    = 47320.0,
        level         = loyaltyLevels[1],
        memberSince   = "Март 2023",
    )

    val orders = listOf(
        Order(
            id = "ORD-20240607-001",
            date = "Сегодня, 14:23",
            items = listOf(CartItem(products[0], 1), CartItem(products[4], 2)),
            total = 1687.0,
            status = OrderStatus.PROCESSING,
            address = "ул. Ленина, 45, кв. 12",
        ),
        Order(
            id = "ORD-20240606-003",
            date = "Вчера, 10:15",
            items = listOf(CartItem(products[2], 3), CartItem(products[8], 2)),
            total = 885.0,
            status = OrderStatus.DELIVERED,
            address = "ул. Ленина, 45, кв. 12",
        ),
        Order(
            id = "ORD-20240603-002",
            date = "3 июня",
            items = listOf(CartItem(products[11], 1), CartItem(products[6], 3)),
            total = 1487.0,
            status = OrderStatus.DELIVERED,
        ),
    )

    val notifications = listOf(
        Notification(1,  "🎁 Бонус за покупку начислен",       "Вам начислено 350 бонусов за покупку на 1 687 ₽",              "Сегодня, 14:23", NotificationType.BONUS),
        Notification(2,  "🔥 Скидка только для вас",           "−20% на всё молочное до конца недели",                          "Сегодня, 10:00", NotificationType.PROMO),
        Notification(3,  "📦 Заказ доставлен",                 "Ваш заказ ORD-20240606-003 успешно доставлен",                  "Вчера, 16:30",   NotificationType.ORDER, isRead = true),
        Notification(4,  "⭐ Двойные бонусы в выходные",       "В субботу и воскресенье кэшбэк ×2 на всё",                      "3 июня",         NotificationType.PROMO, isRead = true),
        Notification(5,  "🎂 С днём рождения!",                "Поздравляем! Вам подарено 500 бонусов",                         "20 мая",         NotificationType.BONUS, isRead = true),
        Notification(6,  "🆕 Новинки в RELAX",                 "Манго Alfonso и авокадо Hass уже в магазине",                   "15 мая",         NotificationType.PROMO, isRead = true),
    )
}
