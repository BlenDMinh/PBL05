import Chessground from "@react-chess/chessground";
import { useEffect, useState } from "react";
import { Chess, SQUARES }  from "chess.js"

import "src/pages/gamev2/chessground.base.css"
import "src/pages/gamev2/chessground.brown.css"
import "src/pages/gamev2/chessground.cburnett.css"
import { Key } from "chessground/types";

export default function GameV2() {
    const [chess, setChess] = useState(new Chess())
    const [pendingMove, setPendingMove] = useState<Key[]>()
    const [selectVisible, setSelectVisible] = useState(false)
    const [fen, setFen] = useState("")
    const [lastMove, setLastMove] = useState<Key[]>()

    useEffect(() => {
        console.log(chess)
    }, [])

    const onMove = (from: Key, to: Key) => {
        const moves = chess.moves({ verbose: true })
        console.log(moves)
        for (let i = 0, len = moves.length; i < len; i++) { /* eslint-disable-line */
            if (moves[i].flags.indexOf("p") !== -1 && moves[i].from === from) {
                setPendingMove([from, to])
                setSelectVisible(true)
                return
            }
        }
        if (chess.move({ from, to, promotion: "x" })) {
            setFen(chess.fen())
            setLastMove([from, to])
            // setTimeout(randomMove, 500)
        }
    }

    const randomMove = () => {
        const moves = chess.moves({ verbose: true })
        const move = moves[Math.floor(Math.random() * moves.length)]
        if (moves.length > 0) {
            chess.move(move.san)
            setFen(chess.fen())
            setLastMove([move.from, move.to])
        }
    }

    const promotion = e => {
        const from = pendingMove[0]
        const to = pendingMove[1]
        chess.move({ from, to, promotion: e })
        setFen(chess.fen())
        setLastMove([from, to])
        setSelectVisible(false)
        setTimeout(randomMove, 500)
    }

    const turnColor = () => {
        return chess.turn() === "w" ? "white" : "black"
    }

    const calcMovable = () => {
        const dests = new Map()
        SQUARES.forEach(s => {
          const ms = chess.moves({ square: s, verbose: true })
          if (ms.length) dests.set(s, ms.map(m => m.to))
        })
        return dests
      }
    
    return <>
        <Chessground config={{
            turnColor: "black",
            fen: fen,
            draggable: {
                enabled: false
            },
            movable: {
                free: false,
                dests: calcMovable(),
                color: "both"
            },
            events: {
                move: onMove
            },
            lastMove: lastMove,
        }} />
    </>
}