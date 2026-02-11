/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Client } from './Client';
import type { Competence } from './Competence';
export type Mission = {
    id?: number;
    titre?: string;
    description?: string;
    budget?: number;
    duree?: string;
    statut?: Mission.statut;
    client?: Client;
    competences?: Array<Competence>;
};
export namespace Mission {
    export enum statut {
        EN_ATTENTE = 'EN_ATTENTE',
        ACCEPTEE = 'ACCEPTEE',
        TERMINEE = 'TERMINEE',
        REFUSEE = 'REFUSEE',
    }
}

