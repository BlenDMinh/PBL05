import { Sprite } from "@pixi/react"
import { chessPiecesTextures } from "../constants/texture"
import { OutlineFilter } from "pixi-filters"
import { useEffect, useMemo, useState } from "react"
import GameContextProps from "../interfaces/gameContext.interface"
import { ChessPieceType } from "../types/game.type"

interface ChessPieceComponentProps extends GameContextProps {
    x: number,
    y: number,
    width: number,
    height: number,
    index: number
    isSelected: boolean
    type: ChessPieceType
}

export default function ChessPieceComponent(props: ChessPieceComponentProps) {
    const [outlineAlpha, setOutlineAlpha] = useState(props.isSelected ? 0.6 : 0.2)
    useEffect(() => {
        if(props.isSelected) {
            setOutlineAlpha(0.6)
        } else {
            setOutlineAlpha(0.2)
        }
    }, [props.game.selectedGridIndex])
    const outlineFilter = useMemo(() => new OutlineFilter(2, 0xffffff, 1, outlineAlpha), [outlineAlpha])

    const game = props.game

    return <Sprite
        x={props.x}
        y={props.y}
        width={props.width}
        height={props.height}
        image={chessPiecesTextures[props.type]}
        filters={[outlineFilter]}
        eventMode="static"
        onmouseenter={() => {
            if(!props.isSelected) {
                setOutlineAlpha(0.4)
            }
        }}
        onmouseleave={() => {
            if(!props.isSelected) {
                setOutlineAlpha(0.2)
            }
        }}
        click={() => {
            console.log("Hello")
            if(!props.isSelected) {
                game.setSelectedGridIndex(props.index)
            } else {
                game.setSelectedGridIndex(-1)
            }
        }}
    >
    </Sprite>
}