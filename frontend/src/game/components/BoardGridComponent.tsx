import { Sprite } from "@pixi/react"
import { useState } from "react"
import { boardTextures } from "../constants/texture"
import GameContextProps from "../interfaces/gameContext.interface"
import { useGame } from "../contexts/game.context"

interface BoardComponentGridProp extends GameContextProps {
    gridX: number,
    gridY: number,
    x: number,
    y: number,
    width: number,
    height: number,
    type: 'dark' | 'light',
    highlightMove?: boolean
}

export default function BoardComponentGrid(props: BoardComponentGridProp) {
    const [highlightSpriteVisible, setHighlightSpriteVisible] = useState(false);

    // Update visibility of highlight sprite based on props
    if (props.highlightMove !== highlightSpriteVisible) {
        setHighlightSpriteVisible(props.highlightMove ?? false);
    }

    const game = props.game

    return (
        <>
            <Sprite
                x={props.x}
                y={props.y}
                width={props.width}
                height={props.height}
                image={boardTextures[props.type]}
            />
            {props.highlightMove && (
                <Sprite
                    x={props.x}  // Adjust position if necessary
                    y={props.y}  // Adjust position if necessary
                    width={props.width}  // Adjust size if necessary
                    height={props.height}  // Adjust size if necessary
                    image={"/game/board/circle-xxl.png"}
                    visible={highlightSpriteVisible}
                    eventMode="static"
                    click={() => {
                        game.doMove({
                            from: game.getCurrentSelectedChessPiece()!.position,
                            to: {
                                x: props.gridX,
                                y: props.gridY,
                            }
                        })
                    }}
                />
            )}
        </>
    )
}