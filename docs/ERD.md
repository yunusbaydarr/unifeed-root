# ERD

Users own social content, clubs and messages. Posts have media, likes and one-level comments; follows is self-referencing. Clubs have members, events and private threads. Direct chat uses conversation threads and participants. All non-junction tables have UUID, audit timestamps, soft-delete and version fields.
