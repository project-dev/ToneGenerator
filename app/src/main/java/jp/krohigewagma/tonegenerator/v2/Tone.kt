package jp.krohigewagma.tonegenerator.v2

/**
 * 音の定義
 * @param key 音階
 * @param oct オクターブ
 */
enum class Tone(val oct : Int, val key : Int) {
    NONE(0, 0),
    C1(1, 1),
    C1s(1, 2),
    D1b(1, 2),
    D1(1, 3),
    D1s(1, 4),
    E1b(1, 4),
    E1(1, 5),
    F1(1, 6),
    F1s(1, 7),
    G1b(1, 7),
    G1(1, 8),
    G1s(1, 9),
    A1b(1, 9),
    A1(1, 10),
    A1s(1, 11),
    B1b(1, 11),
    B1(1, 12),

    C2(2, 1),
    C2s(2, 2),
    D2b(2, 2),
    D2(2, 3),
    D2s(2, 4),
    E2b(2, 4),
    E2(2, 5),
    F2(2, 6),
    F2s(2, 7),
    G2b(2, 7),
    G2(2, 8),
    G2s(2, 9),
    A2b(2, 9),
    A2(2, 10),
    A2s(2, 11),
    B2b(2, 11),
    B2(2, 12),

    C3(4, 1),
    C3s(4, 2),
    D3b(4, 2),
    D3(4, 3),
    D3s(4, 4),
    E3b(4, 4),
    E3(4, 5),
    F3(4, 6),
    F3s(4, 7),
    G3b(4, 7),
    G3(4, 8),
    G3s(4, 9),
    A3b(4, 9),
    A3(4, 10),
    A3s(4, 11),
    B3b(4, 11),
    B3(4, 12),

    C4(8, 1),
    C4s(8, 2),
    D4b(8, 2),
    D4(8, 3),
    D4s(8, 4),
    E4b(8, 4),
    E4(8, 5),
    F4(8, 6),
    F4s(8, 7),
    G4b(8, 7),
    G4(8, 8),
    G4s(8, 9),
    A4b(8, 9),
    A4(8, 10),
    A4s(8, 11),
    B4b(8, 11),
    B4(8, 12),

    C5(16, 1),
    C5s(16, 2),
    D5b(16, 2),
    D5(16, 3),
    D5s(16, 4),
    E5b(16, 4),
    E5(16, 5),
    F5(16, 6),
    F5s(16, 7),
    G5b(16, 7),
    G5(16, 8),
    G5s(16, 9),
    A5b(16, 9),
    A5(16, 10),
    A5s(16, 11),
    B5b(16, 11),
    B5(16, 12),

}