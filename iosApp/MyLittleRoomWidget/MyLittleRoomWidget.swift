import WidgetKit
import SwiftUI

// MARK: - Data Models

struct HabitEntry: TimelineEntry {
    let date: Date
    let level: Int
    let characterEmoji: String
    let characterName: String
    let habits: [HabitItem]
    let completedCount: Int
    let totalCount: Int
}

struct HabitItem: Identifiable {
    let id: String
    let title: String
    let emoji: String
    let isCompleted: Bool
}

// MARK: - Timeline Provider

struct HabitTimelineProvider: TimelineProvider {
    func placeholder(in context: Context) -> HabitEntry {
        sampleEntry()
    }

    func getSnapshot(in context: Context, completion: @escaping (HabitEntry) -> Void) {
        completion(sampleEntry())
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<HabitEntry>) -> Void) {
        // TODO: Read from shared KMP database via App Groups
        let entry = sampleEntry()
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 30, to: Date())!
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }

    private func sampleEntry() -> HabitEntry {
        HabitEntry(
            date: Date(),
            level: 1,
            characterEmoji: "✨",
            characterName: "별먼지",
            habits: [
                HabitItem(id: "1", title: "물 마시기", emoji: "💧", isCompleted: true),
                HabitItem(id: "2", title: "운동하기", emoji: "💪", isCompleted: false),
                HabitItem(id: "3", title: "독서하기", emoji: "📚", isCompleted: false)
            ],
            completedCount: 1,
            totalCount: 3
        )
    }
}

// MARK: - Widget Views

struct HabitWidgetEntryView: View {
    var entry: HabitEntry
    @Environment(\.widgetFamily) var family

    var body: some View {
        switch family {
        case .systemSmall:
            SmallWidgetView(entry: entry)
        case .systemMedium:
            MediumWidgetView(entry: entry)
        case .accessoryCircular:
            CircularLockScreenView(entry: entry)
        case .accessoryRectangular:
            RectangularLockScreenView(entry: entry)
        default:
            MediumWidgetView(entry: entry)
        }
    }
}

// MARK: - Small Widget

struct SmallWidgetView: View {
    let entry: HabitEntry

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(entry.characterEmoji)
                    .font(.title2)
                VStack(alignment: .leading) {
                    Text("Lv.\(entry.level)")
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundColor(Color(hex: "#AD3362"))
                    Text(entry.characterName)
                        .font(.caption2)
                        .foregroundColor(.secondary)
                }
            }

            Spacer()

            HStack {
                Text("\(entry.completedCount)/\(entry.totalCount)")
                    .font(.title3)
                    .fontWeight(.bold)
                    .foregroundColor(Color(hex: "#AD3362"))
                Text("완료")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding()
        .containerBackground(for: .widget) {
            Color(hex: "#FFFBFC")
        }
    }
}

// MARK: - Medium Widget

struct MediumWidgetView: View {
    let entry: HabitEntry

    var body: some View {
        HStack(spacing: 12) {
            // Left: Character info
            VStack(alignment: .leading, spacing: 4) {
                Text(entry.characterEmoji)
                    .font(.largeTitle)
                Text("Lv.\(entry.level)")
                    .font(.caption)
                    .fontWeight(.bold)
                    .foregroundColor(Color(hex: "#AD3362"))
                Text(entry.characterName)
                    .font(.caption2)
                    .foregroundColor(.secondary)
                Spacer()
                Text("\(entry.completedCount)/\(entry.totalCount) 완료")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            .frame(maxWidth: 80)

            // Right: Habit list
            VStack(alignment: .leading, spacing: 4) {
                ForEach(entry.habits.prefix(4)) { habit in
                    HStack(spacing: 4) {
                        Text(habit.emoji)
                            .font(.caption)
                        Text(habit.title)
                            .font(.caption)
                            .lineLimit(1)
                            .foregroundColor(habit.isCompleted ? .secondary : .primary)
                            .strikethrough(habit.isCompleted)
                        Spacer()
                        Text(habit.isCompleted ? "✅" : "⬜")
                            .font(.caption2)
                    }
                    .padding(.vertical, 2)
                    .padding(.horizontal, 6)
                    .background(
                        RoundedRectangle(cornerRadius: 6)
                            .fill(habit.isCompleted
                                  ? Color(hex: "#FDE4ED")
                                  : Color(hex: "#FFF0F5"))
                    )
                }
            }
        }
        .padding()
        .containerBackground(for: .widget) {
            Color(hex: "#FFFBFC")
        }
    }
}

// MARK: - Lock Screen Widgets (iOS 16+)

struct CircularLockScreenView: View {
    let entry: HabitEntry

    var body: some View {
        ZStack {
            AccessoryWidgetBackground()
            VStack(spacing: 1) {
                Text(entry.characterEmoji)
                    .font(.caption)
                Text("\(entry.completedCount)/\(entry.totalCount)")
                    .font(.caption2)
                    .fontWeight(.bold)
            }
        }
    }
}

struct RectangularLockScreenView: View {
    let entry: HabitEntry

    var body: some View {
        HStack {
            Text(entry.characterEmoji)
            VStack(alignment: .leading) {
                Text("Lv.\(entry.level) \(entry.characterName)")
                    .font(.caption)
                    .fontWeight(.bold)
                Text("\(entry.completedCount)/\(entry.totalCount) 습관 완료")
                    .font(.caption2)
            }
        }
    }
}

// MARK: - Widget Configuration

@main
struct MyLittleRoomWidget: Widget {
    let kind: String = "MyLittleRoomWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: HabitTimelineProvider()) { entry in
            HabitWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("My Little Room")
        .description("오늘의 습관을 확인하고 체크하세요")
        .supportedFamilies([
            .systemSmall,
            .systemMedium,
            .accessoryCircular,
            .accessoryRectangular
        ])
    }
}

// MARK: - Color Extension

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 6:
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8:
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

// MARK: - Preview

#Preview(as: .systemMedium) {
    MyLittleRoomWidget()
} timeline: {
    HabitEntry(
        date: Date(),
        level: 3,
        characterEmoji: "⭐",
        characterName: "작은 별",
        habits: [
            HabitItem(id: "1", title: "물 마시기", emoji: "💧", isCompleted: true),
            HabitItem(id: "2", title: "운동하기", emoji: "💪", isCompleted: false),
            HabitItem(id: "3", title: "독서하기", emoji: "📚", isCompleted: false)
        ],
        completedCount: 1,
        totalCount: 3
    )
}
